package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.Message.InfinityMessage;
import link.dwsy.ddl.XO.RB.CreateQuestionRB;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.constants.mq.InfinityMQConstants;
import link.dwsy.ddl.constants.mq.QuestionSearchMQConstants;
import link.dwsy.ddl.constants.question.QuestionRedisKey;
import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.*;
import link.dwsy.ddl.repository.QA.*;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.QuestionRedisRecordService;
import link.dwsy.ddl.service.Impl.UserStateService;
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

    @Resource
    private UserStateService userStateService;

    @Override
    public PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest) {
        questionStateCollection.removeAll(Set.of(QuestionState.HIDE, QuestionState.UNRESOLVED, QuestionState.AUDITING));
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndQuestionStateInAndUser_DeletedFalse(questionStateCollection, pageRequest);

        return new PageData<>(questionFields);
    }

    public PageData<QaQuestionField> getPageListManage(long userId, QuestionState questionState, PageRequest pageRequest) {
//        questionStateCollection.removeAll(Set.of(QuestionState.HIDE,QuestionState.UNRESOLVED,QuestionState.AUDITING));
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndUserIdAndQuestionState(userId, questionState, pageRequest);
        return new PageData<>(questionFields);
    }


    @Override
    public QaQuestionField getQuestionById(long qid, boolean getQuestionComment) {
        if (qaQuestionFieldRepository.userIsCancelled(qid) != 0) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        QaQuestionField questionField = qaQuestionFieldRepository.findByDeletedFalseAndIdAndQuestionStateNot(qid, QuestionState.HIDE);
        if (questionField == null) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        if (getQuestionComment) {
            PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", 1, 8);
            Page<QaAnswer> questionComment = qaAnswerRepository
                    .findByDeletedFalseAndQuestionField_IdAndAnswerType(qid, AnswerType.comment, pageRequest);
            for (QaAnswer qaAnswer : questionComment) {
                userStateService.cancellationUserHandel(qaAnswer.getUser());
            }
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
        String title = createQuestionRB.getTitle();
        title = title.trim().replaceAll("\n", "");
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
            createQuestionRB.setSummary(pure.substring(0, Math.min(pure.length(), 200)));
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
                .questionState(createQuestionRB.getQuestionState())
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
        long saveId = save.getId();
        qaContentRepository.setQuestionFieldLd(saveId, save.getQaQuestionContent().getId());
        if (createQuestionRB.getQuestionState() == QuestionState.ASK) {
            rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_CREATE, saveId);

            if (createQuestionRB.isSendInfinity()) {
                InfinityMessage infinityMessage = InfinityMessage.builder().infinityType(InfinityType.Question).refId(saveId).build();
                rabbitTemplate.convertAndSend(InfinityMQConstants.QUEUE_DDL_INFINITY_SEND, infinityMessage);
            }
        } else {
            rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_DELETE, saveId);
        }

        return saveId;
    }

    public long updateQuestion(CreateQuestionRB createQuestionRB) {
        String title = createQuestionRB.getTitle();
        title = title.trim().replaceAll("\n", "");
        QuestionState questionState = createQuestionRB.getQuestionState();
        //此次ask为还原状态
        Set<QuestionState> allowState = Set.of(QuestionState.ASK, QuestionState.HIDE, QuestionState.DRAFT);
        if (!allowState.contains(questionState)) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        Long questionId = createQuestionRB.getQuestionId();
        Long userIdByQuestionId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
        if (!(userIdByQuestionId.equals(currentUser.getId()))) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }

        ArrayList<QaTag> qaTags = new ArrayList<>(qaQuestionTagRepository.findAllById(createQuestionRB.getQuestionTagIds().stream().distinct().collect(Collectors.toList())));


        QaGroup qaGroup = qaGroupRepository.findById(createQuestionRB.getQuestionGroupId()).orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));

        String html = HtmlHelper.toHTML(createQuestionRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(createQuestionRB.getSummary())) {
            createQuestionRB.setSummary(pure.substring(0, Math.min(200, pure.length())));
        }

        QaQuestionField field = qaQuestionFieldRepository.findByDeletedFalseAndId(questionId);
        QaQuestionContent questionContent = qaContentRepository.findByDeletedFalseAndQuestionFieldId(questionId);
        //历史版本保存
        int version = field.getVersion() + 1;
        if (questionState == QuestionState.ASK || questionState == QuestionState.DRAFT) {
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionTitleKey + field.getId(), title);
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionFieldKey + field.getId(), JSON.toJSONString(field));
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionContentKey + field.getId(), questionContent.getTextMd());
            redisTemplate.opsForList().rightPush(QuestionRedisKey.QuestionHistoryVersionCreateDateKey + field.getId(), String.valueOf(System.currentTimeMillis()));
        }
        questionContent.setTextMd(createQuestionRB.getContent());
        questionContent.setTextHtml(html);
        questionContent.setTextPure(pure);
        field.setVersion(version);
        field.setTitle(title);

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
        QuestionState fieldQuestionState = field.getQuestionState();
        if (Set.of(QuestionState.HAVE_ANSWER, QuestionState.RESOLVED, QuestionState.UNRESOLVED, QuestionState.ASK).contains(fieldQuestionState)) {
            if (questionState == QuestionState.HIDE || questionState == QuestionState.DRAFT) {
                field.setBeforeQuestionState(fieldQuestionState);
                field.setQuestionState(questionState);
            }
        } else if (fieldQuestionState == QuestionState.HIDE || fieldQuestionState == QuestionState.DRAFT) {
            if (questionState == QuestionState.ASK) {
                if (field.getBeforeQuestionState() != null) {
                    field.setQuestionState(field.getBeforeQuestionState());
                } else {
                    field.setQuestionState(QuestionState.ASK);
                }


                field.setBeforeQuestionState(null);
            }
        }

        QaQuestionField save = qaQuestionFieldRepository.save(field);
//        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());
        if (questionState == QuestionState.ASK) {
            rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_CREATE, save.getId());
        } else {
            rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_DELETE, save.getId());
        }
//        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }

    @Resource
    private QuestionRedisRecordService questionRedisRecordService;

    public void view(Long id) {

        questionRedisRecordService.record(id, RedisRecordHashKey.view, 1);
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


    public PageData<QaQuestionField> getPageListByUserId(Long userId, QuestionState state, PageRequest pageRequest) {
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndUserIdAndQuestionState(userId, state, pageRequest);
        return new PageData<>(questionFields);
    }
}
