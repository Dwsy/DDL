package link.dwsy.ddl.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@SpringBootTest
public class article_TagGroupTest {


    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleContentRepository articleContentRepository;

    @Resource
    UserRepository userRepository;

    @Resource

    ArticleFieldRepository articleFieldRepository;

    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Test
    public void saveTag() {
        ArticleTag t1 = ArticleTag.builder().name("Java").tagInfo("info").build();
        ArticleTag t2 = ArticleTag.builder().name("C++").tagInfo("info").build();
        ArticleTag t3 = ArticleTag.builder().name("Rust").tagInfo("info").build();
        ArticleTag t4 = ArticleTag.builder().name("GO").tagInfo("info").build();
        List<ArticleTag> articleTags = Arrays.asList(t1, t2, t3, t4);
        articleTagRepository.saveAll(articleTags);
        articleTagRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void updateTag(){
        ArticleTag articleTag = articleTagRepository.findById(1L).get();
        articleTag.setTagInfo("{1}");
        articleTagRepository.save(articleTag);
    }

    @Test
    public void lmtTest() {
        articleGroupRepository.logicallyDeleteById(1L);
    }

    @Test
    public void saveContent() {
//        Long[] tagSid = {65L, 66L};
//        Set<ArticleTag> tags = new HashSet<>(articleTagRepository.findAllById(Arrays.asList(tagSid)));
//        ArticleGroup articleGroup = articleGroupRepository.findById(63L).get();
//        User user = userRepository.findById(18L).get();
//        ArticleContent articleContent = ArticleContent.builder().textMd("#TEST").textHtml("html").textPure("pure").build();
//        ArticleField build1 = ArticleField.builder().user(user).articleContent(articleContent)
//                .summary("summary").articleGroup(articleGroup)
//                .title("title").banner("banner.png").articleTags(tags).build();
//        ArticleField build2 = ArticleField.builder().user(user).articleContent(articleContent)
//                .summary("summary").articleGroup(articleGroup)
//                .title("title2").banner("banner.png").articleTags(tags).build();
//        articleFieldRepository.save(build1);

    }

    @Test
    public void getFieldListByTagId() {
//        articleContentRepository.findById(39L);
        long[] ids = articleTagRepository.findArticleContentIdListById(1L);

        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByIdInAndDeletedIsFalseAndArticleState
                        (ids, ArticleState.open, PageRequest.of(0, 10));

    }

    @Test
    public void getFieldListByGroupId() throws JsonProcessingException {
//        articleContentRepository.findById(39L);
        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByDeletedIsFalseAndArticleGroupIdAndArticleState
                        (2L, ArticleState.open, PageRequest.of(0, 10));
        System.out.println(new ObjectMapper().writeValueAsString(fieldVO));
//        fieldVO.forEach(System.out::println);
//        articleTagRepository.getFieldListByTagId();
//        articleContentRepository.findAll().forEach(System.out::println);

    }

    @Test
    public void group() {
        articleGroupRepository.save(ArticleGroup.builder().name("前端").build());
        articleGroupRepository.save(ArticleGroup.builder().name("后端").build());
    }

    @Test
    public void listByTag() {
        for (fieldVO articleField : articleFieldRepository.findByDeletedFalseAndArticleStateAndArticleTags_Id(ArticleState.open, 1L, PageRequest.of(0, 10))) {
            articleField.getArticleTags().forEach(t->{
                System.out.println(t.getName());
                System.out.println(t.getCreateTime());
            });
            System.out.println("----------");
        }

    }
}
