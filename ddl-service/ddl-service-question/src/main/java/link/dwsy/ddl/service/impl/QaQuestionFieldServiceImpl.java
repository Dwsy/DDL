package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.RB.CreateQuestionRB;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.constants.mq.ArticleSearchConstants;
import link.dwsy.ddl.constants.mq.QuestionSearchConstants;
import link.dwsy.ddl.constants.question.QuestionRedisKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.*;
import link.dwsy.ddl.repository.QA.*;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class QaQuestionFieldServiceImpl implements link.dwsy.ddl.service.QaQuestionFieldService {

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QaQuestionTagRepository qaQuestionTagRepository;

    @Resource
    private QaGroupRepository qaGroupRepository;

    @Resource
    private QaContentRepository qaContentRepository;
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserRepository userRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    @Resource
    private UserFollowingRepository userFollowingRepository;

    @Resource
    private UserCollectionRepository userCollectionRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Override
    public PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest) {
        questionStateCollection.removeAll(Set.of(QuestionState.HIDE, QuestionState.UNRESOLVED, QuestionState.AUDITING));
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndQuestionStateIn(questionStateCollection, pageRequest);
        PageData<QaQuestionField> fieldPageData = new PageData<>(questionFields);
        return fieldPageData;
    }

    public PageData<QaQuestionField> getPageListManage(QuestionState questionState, PageRequest pageRequest) {
//        questionStateCollection.removeAll(Set.of(QuestionState.HIDE,QuestionState.UNRESOLVED,QuestionState.AUDITING));
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndQuestionState(questionState, pageRequest);
        return new PageData<>(questionFields);
    }


    @Override
    public QaQuestionField getQuestionById(long qid, boolean getQuestionComment) {
        QaQuestionField questionField = qaQuestionFieldRepository.findByDeletedFalseAndIdAndQuestionStateNot(qid, QuestionState.HIDE);
        if (getQuestionComment) {
            PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", 1, 8);
            Page<QaAnswer> questionComment = qaAnswerRepository
                    .findByDeletedFalseAndQuestionField_IdAndAnswerType(qid, AnswerType.comment, pageRequest);
            questionField.setQuestionCommentList(questionComment.getContent());
            questionField.setQuestionCommentTotalPages(questionComment.getTotalPages());
            questionField.setQuestionCommentNum(questionComment.getTotalElements());
        }
        return questionField;
    }

    public QaQuestionField getQuestionByIdAndVersion(Long id, int version) {
        String fieldJsonStr = redisTemplate.opsForList().index(QuestionRedisKey.QuestionHistoryVersionFieldKey + id, version);
        if (fieldJsonStr == null) {
            throw new CodeException(CustomerErrorCode.QuestionVersionNotFound);
        }
        return JSON.parseObject(fieldJsonStr, QaQuestionField.class);
    }

    public long createQuestion(CreateQuestionRB createQuestionRB) {
        QuestionState questionState = createQuestionRB.getQuestionState();
        Set<QuestionState> allowState = Set.of(QuestionState.ASK, QuestionState.HIDE, QuestionState.DRAFT);
        if (!allowState.contains(questionState)) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();

        ArrayList<QaTag> qaTags = new ArrayList<>(qaQuestionTagRepository.findAllById(
                createQuestionRB.getQuestionTagIds().stream().distinct().collect(Collectors.toList())
        ));


        QaGroup qaGroup = qaGroupRepository.findById(createQuestionRB.getQuestionGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));

        String html = HtmlHelper.toHTML(createQuestionRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(createQuestionRB.getSummary())) {
            createQuestionRB.setSummary(pure.substring(0, 200));
        }

        QaQuestionContent content = QaQuestionContent.builder()
                .textPure(pure)
                .textMd(createQuestionRB.getContent())
                .textHtml(html)
                .build();

        QaQuestionField field = QaQuestionField.builder()
                .user(userRepository.findUserByIdAndDeletedIsFalse(currentUser.getId()))
                .title(createQuestionRB.getTitle())
                .summary(createQuestionRB.getSummary())
                .questionState(QuestionState.ASK)
                .allowAnswer(true)
                .qaQuestionContent(content)
                .questionTags(qaTags)
                .qaGroup(qaGroup)
                .markDownTheme(createQuestionRB.getMarkDownTheme())
                .markDownThemeDark(createQuestionRB.getMarkDownThemeDark())
                .codeHighlightStyle(createQuestionRB.getCodeHighlightStyle())
                .codeHighlightStyleDark(createQuestionRB.getCodeHighlightStyleDark())
                .build();

        QaQuestionField save = qaQuestionFieldRepository.save(field);
        qaContentRepository.setQuestionFieldLd(save.getId(), save.getQaQuestionContent().getId());
        if (createQuestionRB.getQuestionState() == QuestionState.ASK) {
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE, save.getId());
        }

        return save.getId();
    }

    public long updateQuestion(CreateQuestionRB createQuestionRB) {
        QuestionState questionState = createQuestionRB.getQuestionState();
        Set<QuestionState> allowState = Set.of(QuestionState.ASK, QuestionState.HIDE);
        if (!allowState.contains(questionState)) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        Long questionId = createQuestionRB.getQuestionId();
        Long userIdByQuestionId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
        if (!(userIdByQuestionId.equals(currentUser.getId()))) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }

        ArrayList<QaTag> qaTags = new ArrayList<>(qaQuestionTagRepository.findAllById(
                createQuestionRB.getQuestionTagIds().stream().distinct().collect(Collectors.toList())
        ));


        QaGroup qaGroup = qaGroupRepository.findById(createQuestionRB.getQuestionGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));

        String html = HtmlHelper.toHTML(createQuestionRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(createQuestionRB.getSummary())) {
            createQuestionRB.setSummary(pure.substring(0, 200));
        }

        QaQuestionField field = qaQuestionFieldRepository.findByDeletedFalseAndId(questionId);
        QaQuestionContent questionContent = qaContentRepository.findByDeletedFalseAndQuestionFieldId(questionId);
        //历史版本保存
        int version = field.getVersion() + 1;
        if (questionState == QuestionState.ASK || questionState == QuestionState.DRAFT) {
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionFieldKey + field.getId(), JSON.toJSONString(field));
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionContentKey + field.getId(), questionContent.getTextMd());
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionCreateDateKey + field.getId(), String.valueOf(System.currentTimeMillis()));
        }
        questionContent.setTextMd(createQuestionRB.getContent());
        questionContent.setTextHtml(html);
        questionContent.setTextPure(pure);
        field.setVersion(version);
        field.setTitle(createQuestionRB.getTitle());
        field.setQuestionState(questionState);
//        field.setAllowAnswer(createQuestionRB.getllowAnswer());
        field.setSummary(createQuestionRB.getSummary());
        field.setQaQuestionContent(questionContent);
        field.setQuestionTags(qaTags);
        field.setQaGroup(qaGroup);
        field.setCodeHighlightStyle(createQuestionRB.getCodeHighlightStyle());
        field.setMarkDownTheme(createQuestionRB.getMarkDownTheme());
        field.setCodeHighlightStyleDark(createQuestionRB.getCodeHighlightStyleDark());
        field.setMarkDownThemeDark(createQuestionRB.getMarkDownThemeDark());


        field.setId(questionId);
        QaQuestionField save = qaQuestionFieldRepository.save(field);
//        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());
        if (createQuestionRB.getQuestionState() == QuestionState.ASK) {
            rabbitTemplate.convertAndSend(QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE, save.getId());
        }
        //todo hide search
//        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }

    public void view(Long id) {
        redisTemplate.opsForHash().increment(RedisRecordKey.RedisQuestionRecordKey, id.toString(), 1);
        String num = (String) redisTemplate.opsForHash().get(RedisRecordKey.RedisQuestionRecordKey, id.toString());
        if (num != null && (Integer.parseInt(num)) % 10 == 0) {
            rabbitTemplate.convertAndSend
                    (QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH,
                            QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE, id);
        }
        qaQuestionFieldRepository.viewNumIncrement(id, 1);
    }

    public UserActionVO getUserToQuestionAction(long questionId) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        Long userId = currentUser.getId();
        UserActionVO userActionVO = new UserActionVO();


        QaAnswer answer = qaAnswerRepository.findByDeletedFalseAndUser_IdAndQuestionField_IdAndParentAnswerIdAndAnswerTypeIn(
                userId, questionId, -1L, Set.of(AnswerType.up, AnswerType.down, AnswerType.cancel));
        if (answer != null) {
            AnswerType answerType = answer.getAnswerType();
            userActionVO.setSupport(answerType);
        }
        userActionVO.setCollect(userCollectionRepository
                .existsByUserIdAndSourceIdAndCollectionTypeAndDeletedFalse
                        (userId, questionId, CollectionType.Question));

        Long followUserId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
        if (followUserId != null) {
            userActionVO.setFollow(userFollowingRepository
                    .existsByUserIdAndFollowingUserIdAndDeletedIsFalse(userId, followUserId));
        }

        userActionVO.setWatch(qaQuestionFieldRepository.isWatch(userId, questionId) > 0);
        return userActionVO;
    }

    public boolean watchQuestion(long questionId) {
        Long userId = userSupport.getCurrentUser().getId();
        QaQuestionField questionField = qaQuestionFieldRepository.findByDeletedFalseAndId(questionId);
        if (questionField == null) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        if (qaQuestionFieldRepository.isWatch(userId, questionId) > 0) {
            return false;
        }
        return qaQuestionFieldRepository.watchQuestion(userId, questionId) > 0;
    }

    public boolean unWatchQuestion(long questionId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (qaQuestionFieldRepository.isWatch(userId, questionId) == 0) {
            return false;
        }
        return qaQuestionFieldRepository.cancelWatchQuestion(userId, questionId) > 0;
    }


}
