package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import link.dwsy.ddl.XO.ES.Constants.ArticleSearchConstant;
import link.dwsy.ddl.XO.ES.Constants.QuestionSearchConstant;
import link.dwsy.ddl.XO.ES.Question.QuestionEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.XO.ES.article.ArticleTagSearchDoc;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.service.Impl.QuestionSearchServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/14
 */
@RestController
@RequestMapping("question")
public class QuestionSearchController {

    @Resource
    private ElasticsearchClient client;
    @Resource
    private QuestionSearchServiceImpl questionSearchService;


    @GetMapping("{query}")
    @UserActiveLog
    public PageData<QuestionEsDoc> search(@PathVariable String query,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size
    ) throws IOException {
        // todo 根据点赞 收藏 浏览量 算分排序
        return questionSearchService.getSearchPageData(page, size, query);

    }

    @GetMapping("suggestion/{query}")
    @UserActiveLog
    public List<String> suggestion(@PathVariable String query) throws IOException {

        SearchResponse<ArticleEsDoc> search = client.search(req -> {
                    req.index(QuestionSearchConstant.QuestionEsIndex)
                            .suggest(s ->
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

    @GetMapping("tag/suggestion/{query}")
    @UserActiveLog
    public List<ArticleTagSearchDoc> tagSuggestion(@PathVariable String query) throws IOException {
//
        SearchResponse<ArticleTagSearchDoc> search = client.search(req -> {
                    req.index(ArticleSearchConstant.ArticleTagEsIndex)
                            .suggest(s ->
                                    s.suggesters("suggestion", sug ->
                                            sug.text(query)
                                                    .completion(com ->
                                                            com.field("name")
                                                                    .skipDuplicates(true)
                                                                    .size(6))))
                            .source(config -> config.fetch(true));
//                            .sort(s -> s.field(f -> f.field("articleNum").order(SortOrder.Desc)));
                    return req;
                },
                ArticleTagSearchDoc.class);
        List<ArticleTagSearchDoc> suggestList = search.suggest().get("suggestion").get(0).completion().options()
                .stream().map(CompletionSuggestOption::source).collect(Collectors.toList());

        return suggestList.stream().sorted(Comparator.comparing(ArticleTagSearchDoc::getArticleNum).reversed()).collect(Collectors.toList());

    }
}
