package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.Message.InfinityMessage;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.util.HtmlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class InfinityMessageProcess {
    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private UserNotifyRepository userNotifyRepository;

    @Resource
    private InfinityRepository infinityRepository;

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    public void sendInfinity(InfinityMessage message) {
        InfinityType infinityType = message.getInfinityType();
        long refId = message.getRefId();
        switch (infinityType) {
            case Article:
                sendArticleInfinity(refId);
                break;
            case Question:
                sendQuestionInfinity(refId);
                break;
            case Answer:
                sendAnswerInfinity(refId);
                break;
        }
        log.info("InfinityMessageProcess sendInfinity success id：{} type：{}", refId, infinityType);
    }

    private void sendQuestionInfinity(long refId) {
        QaQuestionField questionField = qaQuestionFieldRepository.findByDeletedFalseAndId(refId);
        Infinity buildInfinity = Infinity.builder()
                .content(questionField.getTitle() + "\n" + questionField.getSummary())
                .refId(refId)
                .type(InfinityType.Question)
                .user(questionField.getUser())
                .build();
        infinityRepository.save(buildInfinity);
    }

    private void sendAnswerInfinity(long refId) {
        qaAnswerRepository.findByDeletedFalseAndId(refId).ifPresent(qaAnswer -> {
            long answerId = qaAnswer.getId();
            String questionTitle = qaQuestionFieldRepository.getTitleByAnswerId(answerId);
            Infinity buildInfinity = Infinity.builder()
                    .content(HtmlHelper.toPure(questionTitle + "\n" + qaAnswer.getTextHtml()))
                    .refId(refId)
                    .type(InfinityType.Answer)
                    .user(qaAnswer.getUser())
                    .build();
            infinityRepository.save(buildInfinity);
        });
    }


    private void sendArticleInfinity(long refId) {
        ArticleField articleField = articleFieldRepository.findByDeletedFalseAndId(refId);
        Infinity buildInfinity = Infinity.builder()
                .content(articleField.getTitle() + "\n" + articleField.getSummary())
                .refId(refId)
                .type(InfinityType.Article)
                .user(articleField.getUser())
                .build();
        if (articleField.getBanner() != null) {
            buildInfinity.setImgUrl1(articleField.getBanner());
        }
        infinityRepository.save(buildInfinity);
    }
}
