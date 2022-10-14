package link.dwsy.ddl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import link.dwsy.ddl.XO.ES.article.ArticleTagSearchDoc;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/10/13
 */
@SpringBootTest
public class ArticleTagSearchTest {
    private final String INDEX = "ddl_article_tag";
    @Resource
    private ElasticsearchClient client;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleContentRepository articleContentRepository;

    @Resource
    private ArticleTagRepository articleTagRepository;

    @Test
    public void indicesByJson() throws IOException {
        ElasticsearchIndicesClient indices = client.indices();
        BooleanResponse exists = indices.exists(e -> e.index(INDEX));
        if (!exists.value()) {
            String filePath = "src/main/resources/EsMappings/articleTag.json";
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
    public void putAllTag() throws IOException {
        List<ArticleTag> allTag = articleTagRepository.findAllByDeletedIsFalse();

        for (ArticleTag tag : allTag) {
            System.out.println("插入：" + tag.getName());
            String groupName;
            if (tag.getArticleGroup() == null) {
                groupName = "未分组";
            } else {
                groupName = tag.getArticleGroup().getName();
            }
            ArticleTagSearchDoc doc = ArticleTagSearchDoc.builder()
//                    .id(tag.getId())
                    .name(tag.getName())
                    .articleNum(tag.getArticleNum())
                    .groupName(groupName)
                    .indexPageDisplay(tag.isIndexPageDisplay())
                    .build();
            Result result = client.create(req ->
                    req.index(INDEX)
                            .id(String.valueOf(tag.getId()))
                            .document(doc)
            ).result();
            System.out.println(result);
        }
    }

    @Test
    public void tagSuggest() throws IOException {
        String query = "ja";
        SearchResponse<ArticleTagSearchDoc> search = client.search(req -> {
                    req.index(INDEX).suggest(s ->
                                    s.suggesters("suggestion", sug ->
                                            sug.text(query)
                                                    .completion(com ->
                                                            com.field("name")
                                                                    .skipDuplicates(true)
                                                                    .size(10))))
                            .source(config -> config.fetch(true));
//                            .sort(s -> s.field(f -> f.field("articleNum").order(SortOrder.Desc)));
                    return req;
                },
                ArticleTagSearchDoc.class);
        List<ArticleTagSearchDoc> collect = search.suggest().get("suggestion").get(0).completion().options().stream().map(CompletionSuggestOption::source).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }


}
