//package link.dwsy.ddl;
//
////import link.dwsy.ddl.XO.VO.TestVORepository;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import link.dwsy.ddl.XO.CustomER.entity.ArticleTagCustom;
//import link.dwsy.ddl.XO.CustomER.repository.ArticleTagRepositoryCustom;
//import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
//import link.dwsy.ddl.repository.Article.ArticleContentRepository;
//import link.dwsy.ddl.XO.CustomER.repository.ArticleContentRepositoryCustom;
//import link.dwsy.ddl.repository.Article.ArticleTagRepository;
//import org.hibernate.Hibernate;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.util.Arrays;
//
///**
// * @Author Dwsy
// * @Date 2022/8/25
// */
//@SpringBootTest
//public class findTest {
//    @Autowired
//    private ArticleContentRepository articleContentRepository;
//
//    @Resource
//    private ArticleContentRepositoryCustom articleContentRepositoryB;
//    @Resource
//    private ArticleCommentRepository articleCommentRepository;
//    @Resource
//    private ArticleTagRepositoryCustom articleTagRepositoryCustom;
//
//    @Resource
//    private ArticleContentRepositoryCustom articleContentRepositoryCustom;
//    @Test
//    public void test() {
//        System.out.println(articleCommentRepository.getCountByArticleId(73L));
//        System.out.println(articleTagRepositoryCustom.getCountByArticleId(73L));
////        System.out.println(articleContentRepositoryCustom.findByTitleIsNotNull());
////        System.out.println(articleContentRepositoryB.findByTitleIsNotNull());
////        articleCommentRepository.findById(78L);
////        articleTagRepository.findById()
////        testVORepository.findAll();
////        System.out.println(articleContentRepository.findByTitleIsNotNull().toString());
////        Page<ArticleList> allByDeletedIsFalse = articleContentRepository.findAllByDeletedIsFalse(PageRequest.of(0, 10));
////        System.out.println(allByDeletedIsFalse);
//    }
//
//    @Test
//
//    public void findTagArticle() {
//
////        Long[] articleContentIdListById = articleTagRepositoryCustom.findArticleContentIdListById(65L);
//        articleContentRepositoryCustom.findByIdAndAndDeletedIsFalse(73L);
////        articleContentRepositoryCustom.findAllByIdIn(Arrays.asList(articleContentIdListById));
////        Long[] aid = articleTagRepositoryCustom.findArticleContentIdListById(65L);
////        articleContentRepositoryCustom.findAllById(Arrays.asList(aid)).forEach(System.out::println);
////        articleTagRepositoryCustom.
////        ArticleTagCustom articleTagCustom = articleTagRepositoryCustom.findById(65L).get();
////        Hibernate.initialize(articleTagCustom.getArticleContents());
////        System.out.println(articleTagCustom);
////        Hibernate.initialize(articleTagRepositoryCustom.findById(65L).get().getArticleContents());
////                Hibernate.initialize
//    }
//
//
//}
