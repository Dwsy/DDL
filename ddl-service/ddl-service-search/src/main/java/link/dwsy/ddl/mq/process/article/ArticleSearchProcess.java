package link.dwsy.ddl.mq.process.article;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleEsSuggestion;
import link.dwsy.ddl.XO.ES.article.ArticleTagEsDoc;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.constants.mq.ArticleSearchConstants;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class ArticleSearchProcess {

    private final String INDEX = ArticleSearchConstants.INDEX;
    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
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
                .commentNum(af.getCommentNum())
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

    public boolean updateOrSaveAllDataById(long aid) {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.published);
        if (af == null) {
            return false;
        }
        String pureTextById = articleContentRepository.getPureTextById(aid);
        ArticleEsDoc articleEsDoc = ArticleEsDoc.builder()
                .id(af.getId())
                .title(af.getTitle())
                .content(pureTextById)
                .summary(af.getSummary())
                .banner(af.getBanner())
                .userNickName(af.getUser().getNickname())
                .userId(String.valueOf(af.getUser().getId()))
                .group(af.getArticleGroup().getName())
                .tagList(af.getArticleTags().stream().map(tag -> ArticleTagEsDoc.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build()).collect(Collectors.toList()))
                .suggestion(ArticleEsSuggestion.
                        create(af.getTitle(), af.getArticleGroup().getName(), af.getArticleTags()))
                .upNum(af.getUpNum())
                .viewNum(af.getViewNum())
                .downNum(af.getDownNum())
                .collectNum(af.getCollectNum())
                .build();
//        ArticleEsDoc.builder().title("updateTest").build();
        try {
            client.update(req -> req
                            .index(INDEX).id(String.valueOf(aid))
                            .doc(articleEsDoc)
                            .docAsUpsert(true)
                    , ArticleEsDoc.class);
        } catch (IOException e) {
            log.info("更新失败 aId ：{}", aid);
            throw new RuntimeException(e);
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
