package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.Index.ArticleIndex;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/14
 */
@RestController
@RequestMapping("article")
public class ArticleSearchController {

    private final String INDEX = ArticleIndex.IndexName;
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    private ElasticsearchClient client;
    @Resource
    private ArticleFieldRepository articleFieldRepository;
    @Resource
    private ArticleContentRepository articleContentRepository;

    @GetMapping("{query}")
    @UserActiveLog
    public List<ArticleEsDoc> search(@PathVariable String query,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size
    ) throws IOException {
        // todo 根据点赞 收藏 浏览量 算分排序
        SearchResponse<ArticleEsDoc> search = client.search(
                req -> {
                    req.index(INDEX)
                            .source(s -> s
                                    .filter(f -> f
                                            .excludes("content", "suggestion")))
                            .query(q -> q
                                    .multiMatch(mm -> mm
                                            .query(query)
                                            .fields("title", "content", "userNickName.name","group","tagList.name"))
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
            retSearch.add(source);
        }

        return retSearch;

    }


    @GetMapping("suggestion/{query}")
    @UserActiveLog
    public List<String> suggestion(@PathVariable String query) throws IOException {

        SearchResponse<ArticleEsDoc> search = client.search(req -> {
                    req.suggest(s ->
                                    s.suggesters("suggestion", sug ->
                                            sug.text(query)
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
        return collect;
    }
}
