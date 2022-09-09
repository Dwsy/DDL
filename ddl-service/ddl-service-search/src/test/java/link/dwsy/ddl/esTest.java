package link.dwsy.ddl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleEsSuggestion;
import link.dwsy.ddl.XO.ES.article.ArticleTagEsDoc;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

@SpringBootTest
public class esTest {
    @Resource
    private ElasticsearchClient client;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleContentRepository articleContentRepository;

    private String INDEX = "ddl_article";

    @Test
    public void indices() throws IOException {
        ElasticsearchIndicesClient indices = client.indices();
        BooleanResponse exists = indices.exists(e -> e.index("test"));
        if (!exists.value()) {
            indices.create(e -> e.index("test"));
            System.out.println("创建索引");
        } else {
            System.out.println("索引已存在");
            indices.delete(d -> d.index("test"));
            System.out.println("删除索引");
        }
    }

    @Test
    public void Test() throws IOException {
//        indexRequest(9L);
//        GetRequest build = new GetRequest.Builder().index(INDEX).id("9").build();
//        get(1L);
        for (Long aid : articleFieldRepository.findAllId()) {
            indexRequest(aid);
        }
    }

    private void get(long aid) throws IOException {
        GetResponse<ArticleEsDoc> response = client.get(g -> g
                        .index(INDEX)
                        .id(String.valueOf(aid)),
                ArticleEsDoc.class);
        ArticleEsDoc source = response.source();
        System.out.println(source);
    }

    public void indexRequest(long aid) throws IOException {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.open);
        String pureTextById = articleContentRepository.getPureTextById(aid);
        System.out.println("插入：" + af.getTitle());
        ArticleEsDoc articleEsDoc = ArticleEsDoc.builder()
                .id(af.getId())
                .title(af.getTitle())
                .content(pureTextById)
                .userNickName(af.getUser().getNickname())
                .userId(String.valueOf(af.getUser().getId()))
                .group(af.getArticleGroup().getName())
                .tagList(af.getArticleTags().stream().map(tag -> ArticleTagEsDoc.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build()).collect(Collectors.toList()))
                .suggestion(ArticleEsSuggestion.
                        create(af.getTitle(), af.getArticleGroup().getName(), af.getArticleTags()))
                .build();

        Result result = client.create(req ->
                req.index(INDEX)
                        .id(String.valueOf(af.getId()))
                        .document(articleEsDoc)
        ).result();

        System.out.println(result);
    }

    @Test
    public void updateData() {

    }
    @Test
    public void q() throws IOException {
        oder("title", 1, 10);
    }

    public void search(String query) throws IOException {
        SearchResponse<ArticleEsDoc> search = client.search(
                req -> {
                    req.index(INDEX)
                            .query(
                                    q -> q.match(
                                            m -> m.field("all").query(query)

                                    )
                            );
                    return req;
                }
                , ArticleEsDoc.class);
        HitsMetadata<ArticleEsDoc> hits = search.hits();
        List<Hit<ArticleEsDoc>> hits1 = hits.hits();
        for (Hit<ArticleEsDoc> articleEsDocHit : hits1) {
            assert articleEsDocHit.source() != null;
            System.out.println(articleEsDocHit.source().getTitle());
        }
    }

    private void oder(String query, int page, int size) {
        try {
            SearchResponse<ArticleEsDoc> search = client.search(
                    req -> {
                        req.index(INDEX)
                                .query(q ->
                                        q.match(
                                                m -> m.field("all").query(query)
                                        )
                                )
                                .from((page - 1) * size)
                                .size(size)
                                .sort(so -> so.field(f -> f.field("viveNum").field("upNum")));
//                                .highlight(h -> h.fields("title",
//                                                h1 -> h1.preTags("<p class=\"highlight\">")
//                                                        .postTags("</p>"))
//                                        .fields("content",
//                                                h1 -> h1.preTags("<p class=\"highlight\">")
//                                                        .postTags("</p>"))
//                                        .fields("tagList.name",
//                                                h1 -> h1.preTags("<p class=\"highlight\">")
//                                                        .postTags("</p>"))
//                                );
                        return req;
                    }, ArticleEsDoc.class
            );
            // todo  高亮感觉还是前端做好点
            System.out.println(search);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void suggestion() {
        String text = "后";
        try {
            SearchResponse<ArticleEsDoc> search = client.search(req -> {
                        req.suggest(s ->
                                        s.suggesters("suggestion", sug ->
                                                sug.text(text)
                                                        .completion(com ->
                                                                com.field("suggestion")
                                                                        .skipDuplicates(true)
                                                                        .size(10))))
                                .source(config -> config.fetch(false));
                        return req;
                    },
                    ArticleEsDoc.class);
            Map<String, List<Suggestion<ArticleEsDoc>>> suggest = search.suggest();
            List<Suggestion<ArticleEsDoc>> suggestion = suggest.get("suggestion");
            List<String> collect = suggestion.get(0).completion().options().stream().map(CompletionSuggestOption::text).collect(Collectors.toList());
            for (String s : collect) {
                System.out.println(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}



