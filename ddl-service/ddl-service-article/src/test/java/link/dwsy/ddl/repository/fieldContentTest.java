package link.dwsy.ddl.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleContent;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.util.HtmlHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@SpringBootTest
public class fieldContentTest {

    @Resource
    UserRepository userRepository;

    @Resource
    ArticleTagRepository articleTagRepository;

    @Resource
    ArticleContentRepository articleContentRepository;

    @Resource
    ArticleFieldRepository articleFieldRepository;

    @Resource
    ArticleGroupRepository articleGroupRepository;

    @Test
    public void save() {
        Set<Long> longs = Set.of(1L, 2L, 3L);
        User user = userRepository.findById(3L).get();
        HashSet<ArticleTag> articleTags = new HashSet<>(articleTagRepository.findAllById(longs));
        ArticleGroup articleGroup = articleGroupRepository.findById(1L).get();
        ArticleContent content = ArticleContent.builder()
                .textPure("pure").textMd("mdmdmdmdmdmdmdmdmdmdmdv").textHtml("html").build();

        ArticleField field = ArticleField.builder().articleTags(articleTags).
                articleGroup(articleGroup).articleContent(content)
                .user(user).banner("banner.png").summary("summary").title("123321123312").build();
        ArticleField save = articleFieldRepository.save(field);
        System.out.println(articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId()));
    }

    @Test
    public void save3() {
        save();
        save();
        save();
    }

    @Test
    public void findUpdate() throws JsonProcessingException {
        ArticleField articleField = articleFieldRepository.findById(9L).get();
        articleField.setTitle("xxxxx");
        ArticleField save = articleFieldRepository.save(articleField);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(save));
    }

    @Test
    public void page() throws JsonProcessingException {

        PageRequest of = PageRequest.of(0, 10);
        Page<fieldVO> allByDeletedIsFalseAndArticleState = articleFieldRepository.findAllByDeletedIsFalseAndArticleState(ArticleState.open, of);
        System.out.println(new ObjectMapper().writeValueAsString(allByDeletedIsFalseAndArticleState));

//        List<fieldVO> t = articleFieldRepository.findBySqlTest(ArticleState.open, of);
//
//        System.out.println(new ObjectMapper().writeValueAsString(t));


    }

    @Test
    public void getById() throws JsonProcessingException {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(9L, ArticleState.open);

        System.out.println(new ObjectMapper().writeValueAsString(af));
    }

    @Test
    public void getHtmlContent() throws JsonProcessingException {
        String v = articleContentRepository.getHtmlTextById(2L);

        System.out.println(new ObjectMapper().writeValueAsString(v));
    }

    @Test
    public void ttt() {
        List<Long> aids = Arrays.asList(9L, 10L, 200L);
        System.out.println(articleFieldRepository.countByDeletedIsFalseAndIdInAndUser_Id(aids, 3L));
    }

    @Test
    public void toHtml() {
        List<ArticleContent> all = articleContentRepository.findAll();
        all.forEach(c-> c.setTextHtml(HtmlHelper.toHTML(c.getTextMd())));
        articleContentRepository.saveAll(all);
    }
}
