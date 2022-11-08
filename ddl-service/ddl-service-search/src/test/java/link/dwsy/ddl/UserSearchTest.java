package link.dwsy.ddl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.ES.User.UserEsDoc;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.User.UserRepository;
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
public class UserSearchTest {
    private final String INDEX = "ddl_user";
    @Resource
    private ElasticsearchClient client;


    @Resource
    private UserRepository userRepository;

    @Test
    public void indicesByJson() throws IOException {
        ElasticsearchIndicesClient indices = client.indices();
        BooleanResponse exists = indices.exists(e -> e.index(INDEX));
        if (!exists.value()) {
            String filePath = "src/main/resources/EsMappings/user.json";
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
    public void putAllUser() throws IOException {
        List<User> deletedFalse = userRepository.findByDeletedFalse();
        for (User user : deletedFalse) {
            System.out.println("add：" + user.getNickname());
            UserEsDoc.UserEsDocBuilder esDocBuilder = UserEsDoc.builder()
                    .avatar(user.getUserInfo().getAvatar())
                    .userId(user.getId()).userNickName(user.getNickname());
            Result result = client.create(req ->
                    req.index(INDEX)
                            .id(String.valueOf(user.getId()))
                            .document(esDocBuilder.build())
            ).result();
            System.out.println(result);
        }
    }

    @Test
    public void tagSuggest() throws IOException {
        String query = "taka";
        SearchResponse<UserEsDoc> search = client.search(req -> {
                    req.index(INDEX).suggest(s ->
                                    s.suggesters("suggestion", sug ->
                                            sug.text(query)
                                                    .completion(com ->
                                                            com.field("userNickName")
                                                                    .skipDuplicates(true)
                                                                    .size(10))))
                            .source(config -> config.fetch(true));
//                            .sort(s -> s.field(f -> f.field("articleNum").order(SortOrder.Desc)));
                    return req;
                },
                UserEsDoc.class);
        List<UserEsDoc> collect = search.suggest().get("suggestion").get(0).completion().options().stream().map(CompletionSuggestOption::source).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }


    @Test
    public void wildcardQ() throws IOException {
        String query = "s";

        SearchResponse<UserEsDoc> search = client.search(req -> {
                    req.index(INDEX).query(q ->
                            q.wildcard(w ->
                                    w.field("userNickName").value(query + "*")));
                    return req;
                },
                UserEsDoc.class);
        List<Hit<UserEsDoc>> hits = search.hits().hits();
        for (Hit<UserEsDoc> hit : hits) {
            System.out.println(JSON.toJSONString(hit.source()));
        }
        System.out.println(JSON.toJSONString(hits));
    }

}
