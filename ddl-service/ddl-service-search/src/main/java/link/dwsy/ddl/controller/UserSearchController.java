package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import link.dwsy.ddl.XO.ES.User.UserEsDoc;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/11/8
 */
@RestController
@RequestMapping("user")
public class UserSearchController {
    private static final String INDEX = "ddl_user";
    @Resource
    private ElasticsearchClient client;

    @GetMapping("{query}")
    @UserActiveLog
    public PageData<UserEsDoc> search(@PathVariable String query,
                                      @RequestParam(defaultValue = "1") int page) throws Exception {
        SearchResponse<UserEsDoc> search = client.search(req -> {
                    req.index(INDEX).query(q ->
                            q.wildcard(w ->
                                    w.field("userNickName").value(query + "*")));
                    return req;
                },
                UserEsDoc.class);
        HitsMetadata<UserEsDoc> hits = search.hits();
        List<Hit<UserEsDoc>> hitList = hits.hits();
        List<UserEsDoc> retSearch = hitList.stream().map(Hit::source).collect(Collectors.toList());
        PageData<UserEsDoc> docPageData = new PageData<>();
        docPageData.setContent(retSearch);
        assert hits.total() != null;
        docPageData.setTotalElements(hits.total().value());
        docPageData.setPageNumber(page);
        var size = 10;
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
