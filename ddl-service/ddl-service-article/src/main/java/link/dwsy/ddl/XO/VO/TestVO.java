//package link.dwsy.ddl.XO.VO;
//
//import com.blazebit.persistence.view.EntityView;
//import com.blazebit.persistence.view.IdMapping;
//import link.dwsy.ddl.XO.DTO.articleTagDTO;
//import link.dwsy.ddl.entity.ArticleComment;
//import link.dwsy.ddl.entity.ArticleContent;
//import link.dwsy.ddl.entity.ArticleTag;
//import link.dwsy.ddl.entity.User;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * @Author Dwsy
// * @Date 2022/8/25
// */
//
//@EntityView(ArticleContent.class)
//public interface TestVO {
//    @IdMapping
//    Long getId();
//
//    String getTitle();
//
////    UserVO getUser();
//
//    Set<ArticleTagVO> getArticleTags();
//
////    @EntityView(User.class)
////    interface UserVO {
////        @IdMapping
////        Long getId();
////
////        String getUsername();
////
////    }
//    @EntityView(ArticleTag.class)
//    interface ArticleTagVO {
//        @IdMapping
//        Long getId();
//
//        String getName();
//
//        Long getArticleNum();
//
//        String getTagInfo();
//
//    }
//}
//
