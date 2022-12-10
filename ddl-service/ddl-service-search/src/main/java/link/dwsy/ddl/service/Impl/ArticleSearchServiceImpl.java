package link.dwsy.ddl.service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import link.dwsy.ddl.XO.ES.Constants.ArticleSearchConstant;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.service.ArticleSearchService;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/10/27
 */

@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Resource
    private ElasticsearchClient client;

    @NotNull
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
                                                            .fields("title^100", "content^150", "userNickName.name", "group", "tagList.name")))
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
