package link.dwsy.ddl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.ES.Constants.ArticleSearchConstant;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleEsSuggestion;
import link.dwsy.ddl.XO.ES.article.ArticleTagEsDoc;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.util.PageData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

@SpringBootTest
public class ArticleSearchTest {
    private final String INDEX = "ddl_article";
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    private ElasticsearchClient client;
    @Resource
    private ArticleFieldRepository articleFieldRepository;
    @Resource
    private ArticleContentRepository articleContentRepository;

    @Test
    public void indices() throws IOException {
        ElasticsearchIndicesClient indices = client.indices();
        BooleanResponse exists = indices.exists(e -> e.index(INDEX));
        if (!exists.value()) {
            indices.create(e -> e.index(INDEX));
            System.out.println("创建索引");
        } else {
            System.out.println("索引已存在");
            indices.delete(d -> d.index(INDEX));
            System.out.println("删除索引");
        }
    }

    @Test
    public void indicesByJson() throws IOException {
        ElasticsearchIndicesClient indices = client.indices();
        BooleanResponse exists = indices.exists(e -> e.index(INDEX));
        if (!exists.value()) {
            String filePath = "src/main/resources/EsMappings/article.json";
            create(INDEX, new FileInputStream(filePath));
            System.out.println("创建索引");
        } else {
            System.out.println("索引已存在");
        }
    }

    public void create(String name, InputStream inputStream) throws IOException {
        CreateIndexRequest request = CreateIndexRequest.of(builder -> builder
                .index(name)
                .withJson(inputStream));
        client.indices().create(request);
    }

    @Test
    public void TestPutAll() throws IOException {
//        indexRequest(9L);
//        GetRequest build = new GetRequest.Builder().index(INDEX).id("9").build();
//        get(1L);
        for (Long aid : articleFieldRepository.findAllId()) {
//            indexRequest(aid);
            updateAllDataById(aid);
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
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.published);
        String pureTextById = articleContentRepository.getPureTextById(aid);
        System.out.println("插入：" + af.getTitle());
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
                .createTime(af.getCreateTime())
                .upNum(af.getUpNum())
                .downNum(af.getDownNum())
                .commentNum(af.getCommentNum())
                .commentNum(af.getCommentNum())
                .viewNum(af.getViewNum())
                .build();

        Result result = client.create(req ->
                req.index(INDEX)
                        .id(String.valueOf(af.getId()))
                        .document(articleEsDoc)
        ).result();

        System.out.println(result);
    }


    @Test
    public void upTest() throws IOException {
//        updateDataById(11L);
        updateAllDataById(16L);
    }

    public void updateDataById(long aid) throws IOException {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.published);
        if (af == null) {
            throw new CodeException(1, "更新失败");
        }
        ArticleEsDoc esDoc = ArticleEsDoc.builder()
                .upNum(af.getUpNum())
                .downNum(af.getDownNum())
                .collectNum(af.getCollectNum())
                .viewNum(af.getViewNum())
                .build();
        ArticleEsDoc.builder().title("updateTest").build();
        client.update(req -> req
                        .index(INDEX).id(String.valueOf(aid))
                        .doc(esDoc)
                , ArticleEsDoc.class);
    }

    public void updateAllDataById(long aid) throws IOException {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(aid, ArticleState.published);
        if (af == null) {
            throw new CodeException(1, "更新失败");
        }
        String pureTextById = articleContentRepository.getPureTextById(aid);
        ArticleEsDoc articleEsDoc = ArticleEsDoc.builder()
                .id(af.getId())
                .title(af.getTitle())
                .content(pureTextById)
                .summary(af.getSummary())
                .userNickName(af.getUser().getNickname())
                .userId(String.valueOf(af.getUser().getId()))
                .banner(af.getBanner())
                .group(af.getArticleGroup().getName())
                .tagList(af.getArticleTags().stream().map(tag -> ArticleTagEsDoc.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build()).collect(Collectors.toList()))
                .suggestion(ArticleEsSuggestion.
                        create(af.getTitle(), af.getArticleGroup().getName(), af.getArticleTags()))
                .upNum(af.getUpNum())
                .downNum(af.getDownNum())
                .commentNum(af.getCommentNum())
                .createTime(af.getCreateTime())
                .viewNum(af.getViewNum())
                .build();
        ArticleEsDoc.builder().title("updateTest").build();
        client.update(req -> req
                        .index(INDEX).id(String.valueOf(aid))
                        .doc(articleEsDoc)
                        .docAsUpsert(true)
                , ArticleEsDoc.class);
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
                                .source(s -> s
                                        .filter(f -> f
                                                .excludes("content", "suggestion")))
                                .query(q -> q
                                        .multiMatch(mm -> mm
                                                .query(query)
                                                .fields("title", "content^10", "userNickName.name"))
                                )
                                .from((page - 1) * size)
                                .size(size)
                                .sort(so -> so.field(f -> f.field("viveNum").field("upNum")))
                                .highlight(h -> h
                                        .fields("title",
                                                h1 -> h1.preTags("<em class=\"highlight\">")
                                                        .postTags("</em>"))
                                        .fields("content",
                                                h1 -> h1.preTags("<em class=\"highlight\">")
                                                        .postTags("</em>"))
                                        .fields("userNickName.name",
                                                h1 -> h1.preTags("<em class=\"highlight\">")
                                                        .postTags("</em>"))
                                );
                        return req;
                    }, ArticleEsDoc.class
            );

            HitsMetadata<ArticleEsDoc> hits = search.hits();
            List<Hit<ArticleEsDoc>> hitList = hits.hits();
            List<ArticleEsDoc> retSearch = new ArrayList<>();
            for (Hit<ArticleEsDoc> articleEsDocHit : hitList) {
                ArticleEsDoc source = articleEsDocHit.source();
                Map<String, List<String>> highlight = articleEsDocHit.highlight();
                assert source != null;
                Optional.ofNullable(highlight.get("title")).ifPresent(title -> source.setTitle(title.get(0)));
                Optional.ofNullable(highlight.get("userNickName.name")).ifPresent(n -> source.setUserNickName(n.get(0)));
                Optional.ofNullable(highlight.get("content")).ifPresent(c -> source.setContent(c.get(0)));
                List<String> content = highlight.get("content");
//                todo 字段二选一 但是@JsonIgnore 会让es c反序列化也失效
//                if (content != null) {
//                    source.setContent(content.get(0));
//                } else {
//                    source.setContent(source.getSummary());
//                }

                retSearch.add(source);
            }

            System.out.println(new ObjectMapper().writeValueAsString(retSearch));


//            var collect = hitList.stream().map(Hit::source).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void suggestion() {
        String text = "t";
        try {
            SearchResponse<ArticleEsDoc> search = client.search(req -> {
                        req.index(ArticleSearchConstant.ArticleEsIndex)
                                .suggest(s ->
                                        s.suggesters("suggestion", sug ->
                                                sug.text(text)
                                                        .completion(com ->
                                                                com.field("suggestion")
                                                                        .skipDuplicates(true)
                                                                        .size(10))))
                                .source(config -> config.fetch(false)).index(INDEX);
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

    @Test
    public void sort() throws IOException {
        getSearchPageData(1, 10, "编程语言").getContent().forEach(articleEsDoc -> System.out.println(articleEsDoc.getTitle()));
    }

    public PageData<ArticleEsDoc> getSearchPageData(int page, int size, String query) throws IOException {
        SearchResponse<ArticleEsDoc> search = client.search(
                req -> {
                    req.index(ArticleSearchConstant.ArticleEsIndex)
                            .source(s -> s
                                    .filter(f -> f
                                            .excludes("content", "suggestion")))
                            .query(q -> q
                                    .functionScore(fs -> fs
                                            .query(fq -> fq
                                                    .multiMatch(mm -> mm
                                                            .query(query)
                                                            .fields("title", "content^15", "userNickName.name", "group", "tagList.name")))
                                            .scoreMode(FunctionScoreMode.Sum)
                                            .functions(f -> f.scriptScore
                                                    (s -> s.script
                                                            (script -> script
                                                                    .inline(inline -> inline.source("doc['viewNum'].value*0.04")))
                                                            .script(script -> script
                                                                    .inline(inline -> inline.source("doc['upNum'].value*0.2")))
                                                            .script(script -> script
                                                                    .inline(inline -> inline.source("doc['commentNum'].value*0.1")))
                                                    )
                                            )
                                    )
                            )
                            .from((page - 1) * size)
                            .size(size)
                            .highlight(h -> h
                                    .fields("title",
                                            h1 -> h1.preTags("<em class=\"highlight\">")
                                                    .postTags("</em>"))
                                    .fields("content",
                                            h1 -> h1.preTags("<em class=\"highlight\">")
                                                    .postTags("</em>"))
                                    .fields("userNickName.name",
                                            h1 -> h1.preTags("<em class=\"highlight\">")
                                                    .postTags("</em>"))
                            );
                    return req;
                }, ArticleEsDoc.class
        );
        HitsMetadata<ArticleEsDoc> hits = search.hits();
        List<Hit<ArticleEsDoc>> hitList = hits.hits();
        List<ArticleEsDoc> retSearch = new ArrayList<>();
        for (Hit<ArticleEsDoc> articleEsDocHit : hitList) {
            ArticleEsDoc source = articleEsDocHit.source();
            Map<String, List<String>> highlight = articleEsDocHit.highlight();
            assert source != null;
            Optional.ofNullable(highlight.get("title")).ifPresent(title -> source.setTitle(title.get(0)));
            Optional.ofNullable(highlight.get("userNickName.name")).ifPresent(n -> source.setUserNickName(n.get(0)));
            Optional.ofNullable(highlight.get("content")).ifPresent(c -> source.setContent(c.get(0)));
            List<String> content = highlight.get("content");
//                todo 字段二选一 但是@JsonIgnore 会让es c反序列化也失效
            retSearch.add(source);
        }
        PageData<ArticleEsDoc> docPageData = new PageData<>();
        docPageData.setContent(retSearch);
        assert hits.total() != null;
        docPageData.setTotalElements(hits.total().value());
        docPageData.setPageNumber(page);

        docPageData.setTotalPages((int) Math.ceil((double) hits.total().value() / size));
        docPageData.setPageSize(size);
        if (page == 1) {
            docPageData.setFirst(true);
        }
        if (page == docPageData.getTotalPages()) {
            docPageData.setLast(true);
        }
        if (retSearch.isEmpty()) {
            docPageData.setEmpty(true);
        }
        return docPageData;
    }

}



