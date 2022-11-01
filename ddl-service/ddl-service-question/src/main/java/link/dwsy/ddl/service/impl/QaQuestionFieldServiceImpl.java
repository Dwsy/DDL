package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.RB.CreateQuestionRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.*;
import link.dwsy.ddl.mq.QuestionSearchConstants;
import link.dwsy.ddl.repository.QA.*;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest) {
        questionStateCollection.remove(QuestionState.HIDE);
        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndQuestionStateIn(questionStateCollection, pageRequest);
        PageData<QaQuestionField> fieldPageData = new PageData<>(questionFields);
        return fieldPageData;
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

    public long createQuestion(CreateQuestionRB createQuestionRB) {
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
        //todo search action mq
        qaContentRepository.setQuestionFieldLd(save.getId(), save.getQaQuestionContent().getId());
        rabbitTemplate.convertAndSend(QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_CREATE, save.getId());
//        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }

    public long updateQuestion(CreateQuestionRB createQuestionRB) {
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

        QaQuestionField field = new QaQuestionField()
                .setUser(userRepository.findUserByIdAndDeletedIsFalse(currentUser.getId()))
                .setTitle(createQuestionRB.getTitle())
                .setSummary(createQuestionRB.getSummary())
                .setQuestionState(createQuestionRB.getQuestionState())
                .setAllowAnswer(createQuestionRB.isAllow_answer())
                .setQaQuestionContent(content)
                .setQuestionTags(qaTags)
                .setQaGroup(qaGroup)
                .setMarkDownTheme(createQuestionRB.getMarkDownTheme())
                .setMarkDownThemeDark(createQuestionRB.getMarkDownThemeDark())
                .setCodeHighlightStyle(createQuestionRB.getCodeHighlightStyle())
                .setCodeHighlightStyleDark(createQuestionRB.getCodeHighlightStyleDark());

        field.setId(createQuestionRB.getQuestionId());
        QaQuestionField save = qaQuestionFieldRepository.save(field);
        //todo search action mq
//        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());
        rabbitTemplate.convertAndSend(QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE, save.getId());
//        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }

    public void view(Long id) {
        qaQuestionFieldRepository.viewNumIncrement(id, 1);
    }
}
