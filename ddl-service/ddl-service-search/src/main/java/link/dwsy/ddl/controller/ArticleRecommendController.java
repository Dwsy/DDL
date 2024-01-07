package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.service.Impl.ArticleSearchServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.redis.core.RedisTemplate;
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
 * @Date 2022/9/14
 */
@RestController
@RequestMapping("article")
public class ArticleRecommendController {

    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    private ElasticsearchClient client;
    @Resource
    private ArticleSearchServiceImpl articleSearchService;
    @Resource
    private ArticleFieldRepository articleFieldRepository;


    @GetMapping("recommend/{s}")
    @UserActiveLog
    public List<ArticleField> recommend(@PathVariable String s) throws IOException {
        PageData<ArticleEsDoc> searchPageData = articleSearchService.getSearchPageData(1, 10, s);
        List<Long> ids = searchPageData.getContent().stream().map(ArticleEsDoc::getId).collect(Collectors.toList());
        return articleFieldRepository.findAllById(ids);
//        return articleSearchService.getSearchPageData(page, size, query);

    }



}
