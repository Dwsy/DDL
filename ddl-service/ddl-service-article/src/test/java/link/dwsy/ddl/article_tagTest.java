package link.dwsy.ddl;

import cn.hutool.core.util.ArrayUtil;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.ArticleGroup;
import link.dwsy.ddl.entity.ArticleTag;
import link.dwsy.ddl.repository.ArticleContentRepository;
import link.dwsy.ddl.repository.ArticleGroupRepository;
import link.dwsy.ddl.repository.ArticleTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@SpringBootTest
public class article_tagTest {

    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleContentRepository articleContentRepository;

    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Test
    public void saveTag() {
        ArticleTag t1 = ArticleTag.builder().name("Java").tag_info("info").build();
        ArticleTag t2 = ArticleTag.builder().name("C++").tag_info("info").build();
        ArticleTag t3 = ArticleTag.builder().name("Rust").tag_info("info").build();
        ArticleTag t4 = ArticleTag.builder().name("GO").tag_info("info").build();
        List<ArticleTag> articleTags = Arrays.asList(t1, t2, t3, t4);
        articleTagRepository.saveAll(articleTags);

        articleTagRepository.findAll().forEach(System.out::println);


    }

    @Test
    public void saveContent() {
        Long[] tagSid = {65L, 66L};
        Set<ArticleTag> allById = new HashSet<>(articleTagRepository.findAllById(Arrays.asList(tagSid)));
        Optional<ArticleGroup> byId = articleGroupRepository.findById(62L);
        ArticleContent t1 = ArticleContent.builder().text_md("#TEST").title("Title").
                articleTags(allById).articleGroup(byId.orElse(null)).build();
        ArticleContent t2 = ArticleContent.builder().text_md("#TEST").title("123Title").
                articleTags(allById).articleGroup(byId.orElse(null)).build();
        articleContentRepository.save(t1);
        articleContentRepository.save(t2);
    }

    @Test
    public void getContentList() {
//        articleContentRepository.findById(39L);
        articleContentRepository.findAll().forEach(System.out::println);

    }
    @Test
    public void group() {
        articleGroupRepository.save(ArticleGroup.builder().name("前端").build());
        articleGroupRepository.save(ArticleGroup.builder().name("后端").build());

    }
}
