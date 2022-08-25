package link.dwsy.ddl;

//import link.dwsy.ddl.XO.VO.TestVORepository;
import link.dwsy.ddl.repository.ArticleCommentRepository;
import link.dwsy.ddl.repository.ArticleContentRepository;
import link.dwsy.ddl.XO.CustomER.repository.ArticleContentRepositoryCustom;
import link.dwsy.ddl.repository.ArticleTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@SpringBootTest
public class findTest {
    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Resource
    private ArticleContentRepositoryCustom articleContentRepositoryB;
    @Resource
    private ArticleCommentRepository articleCommentRepository;
    @Resource
    private ArticleTagRepository articleTagRepository;

    @Resource
    private ArticleContentRepositoryCustom articleContentRepositoryCustom;
    @Test
    public void test() {
        System.out.println(articleCommentRepository.getCountByArticleId(73L));
        System.out.println(articleTagRepository.getCountByArticleId(73L));
        long[] articleContentIdListById = articleTagRepository.findArticleContentIdListById(65L);
        System.out.println(Arrays.toString(articleContentIdListById));
        System.out.println(articleContentRepositoryCustom.findByTitleIsNotNull());
//        System.out.println(articleContentRepositoryB.findByTitleIsNotNull());
//        articleCommentRepository.findById(78L);
//        articleTagRepository.findById()
//        testVORepository.findAll();
//        System.out.println(articleContentRepository.findByTitleIsNotNull().toString());
//        Page<ArticleList> allByDeletedIsFalse = articleContentRepository.findAllByDeletedIsFalse(PageRequest.of(0, 10));
//        System.out.println(allByDeletedIsFalse);
    }


}
