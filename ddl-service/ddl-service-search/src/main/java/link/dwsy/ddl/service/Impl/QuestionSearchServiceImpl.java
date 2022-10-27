package link.dwsy.ddl.service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import link.dwsy.ddl.XO.ES.Question.QuestionEsDoc;
import link.dwsy.ddl.mq.QuestionSearchConstants;
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
public class QuestionSearchServiceImpl {

    @Resource
    private ElasticsearchClient client;

    @NotNull
    public PageData<QuestionEsDoc> getSearchPageData(int page, int size, String query) throws IOException {
        SearchResponse<QuestionEsDoc> search = client.search(
                req -> {
                    req.index(QuestionSearchConstants.INDEX)
                            .source(s -> s
                                    .filter(f -> f
                                            .excludes("content", "suggestion")))
                            .query(q -> q
                                    .multiMatch(mm -> mm
                                            .query(query)
                                            .fields("title", "content", "userNickName.name", "group", "tagList.name"))
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
                }, QuestionEsDoc.class
        );
        HitsMetadata<QuestionEsDoc> hits = search.hits();
        List<Hit<QuestionEsDoc>> hitList = hits.hits();
        List<QuestionEsDoc> retSearch = new ArrayList<>();
        for (Hit<QuestionEsDoc> questionEsDocHit : hitList) {
            QuestionEsDoc source = questionEsDocHit.source();
            Map<String, List<String>> highlight = questionEsDocHit.highlight();
            assert source != null;
            Optional.ofNullable(highlight.get("title")).ifPresent(title -> source.setTitle(title.get(0)));
            Optional.ofNullable(highlight.get("userNickName.name")).ifPresent(n -> source.setUserNickName(n.get(0)));
            Optional.ofNullable(highlight.get("content")).ifPresent(c -> source.setContent(c.get(0)));
//                todo 字段二选一 但是@JsonIgnore 会让es c反序列化也失效
            retSearch.add(source);
        }
        PageData<QuestionEsDoc> docPageData = new PageData<>();
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
