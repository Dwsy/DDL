package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import link.dwsy.ddl.XO.ES.Constants.InfinitySearchConstant;
import link.dwsy.ddl.XO.ES.Infinity.InfinityTopicEsDoc;
import link.dwsy.ddl.annotation.UserActiveLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

@RestController
@RequestMapping("/infinity")
public class InfinitySearchController {

    @Resource
    private ElasticsearchClient client;

    @GetMapping("topic/{query}")
    @UserActiveLog
    public List<InfinityTopicEsDoc> tagSuggestion(@PathVariable String query) throws IOException {
//
        SearchResponse<InfinityTopicEsDoc> search = client.search(req -> {
                    req.index(InfinitySearchConstant.InfinityTopicEsIndex)
                            .suggest(s ->
                                    s.suggesters("suggestion", sug ->
                                            sug.text(query)
                                                    .completion(com ->
                                                            com.field("name")
                                                                    .skipDuplicates(true)
                                                                    .size(10))))
                            .sort(s -> s.field(f -> f.field("viewNum").order(SortOrder.Desc)));
                    return req;
                },
                InfinityTopicEsDoc.class);
        //.sorted(Comparator.comparing(InfinityTopicEsDoc::get).reversed())
        return search.suggest().get("suggestion").get(0).completion().options()
                .stream().map(CompletionSuggestOption::source).collect(Collectors.toList());

    }
}
