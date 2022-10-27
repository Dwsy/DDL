package link.dwsy.ddl.mq.process.question;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import link.dwsy.ddl.XO.ES.Question.QuestionEsDoc;
import link.dwsy.ddl.XO.ES.Question.QuestionEsSuggestion;
import link.dwsy.ddl.XO.ES.Question.QuestionTagEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class QuestionSearchProcess {

    private final String INDEX = "ddl_question";
    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
    @Resource
    QaContentRepository qaContentRepository;
    @Resource
    ElasticsearchClient client;

    public boolean updateScoreDataById(long aid) {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.published);
        if (af == null) {
            return false;
        }
        ArticleEsDoc esDoc = ArticleEsDoc.builder()
                .upNum(af.getUpNum())
                .downNum(af.getDownNum())
                .collectNum(af.getCollectNum())
                .viewNum(af.getViewNum())
                .build();
        ArticleEsDoc.builder().title("updateTest").build();
        try {
            client.update(req -> req
                            .index(INDEX).id(String.valueOf(aid))
                            .doc(esDoc)
                    , ArticleEsDoc.class);
        } catch (IOException e) {
            log.info("更新失败 aId ：{}", aid);
            return false;
        }
        return true;
    }

    public boolean updateOrSaveAllDataById(long qid) {

        List<QuestionState> questionStates = Arrays.asList(QuestionState.ASK, QuestionState.HAVE_ANSWER, QuestionState.RESOLVED, QuestionState.UNRESOLVED);
        QaQuestionField qf = qaQuestionFieldRepository.findByDeletedFalseAndIdAndQuestionStateIn(qid, questionStates);
        if (qf == null) {
            return false;
        }

        String pureTextById = qaContentRepository.getPureTextById(qid);
        QuestionEsDoc questionEsDoc = QuestionEsDoc.builder()
                .id(qf.getId())
                .title(qf.getTitle())
                .content(pureTextById)
                .summary(qf.getSummary())
                .userNickName(qf.getUser().getNickname())
                .userId(String.valueOf(qf.getUser().getId()))
                .group(qf.getQaGroup().getName())
                .tagList(qf.getQuestionTags().stream().map(tag -> QuestionTagEsDoc.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build()).collect(Collectors.toList()))
                .suggestion(QuestionEsSuggestion.
                        create(qf.getTitle(), qf.getQaGroup().getName(), qf.getQuestionTags()))
                .createTime(qf.getCreateTime())
                .answerNum(qf.getAnswerNum())
                .viewNum(qf.getViewNum())
                .upNum(qf.getUpNum())
                .downNum(qf.getDownNum())
                .collectNum(qf.getCollectNum())
                .build();
;
        try {
            client.update(req -> req
                            .index(INDEX).id(String.valueOf(qid))
                            .doc(questionEsDoc)
                            .docAsUpsert(true)
                    , QuestionEsDoc.class);
        } catch (IOException e) {
            log.info("更新失败 aId ：{}", qid);
            return false;
//            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean delDocById(long aid) {
        try {
            client.delete(req -> req.index(INDEX).id(String.valueOf(aid)));
        } catch (IOException e) {
            log.info("删除失败 aId ：{}", aid);
            throw new RuntimeException(e);
        }
        return true;
    }

}
