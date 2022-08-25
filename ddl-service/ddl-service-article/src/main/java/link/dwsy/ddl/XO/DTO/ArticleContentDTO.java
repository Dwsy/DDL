//package link.dwsy.ddl.XO.DTO;
//
//import link.dwsy.ddl.XO.Enum.ArticleState;
//import link.dwsy.ddl.entity.ArticleContent;
//import link.dwsy.ddl.entity.ArticleGroup;
//import link.dwsy.ddl.entity.ArticleTag;
//import lombok.Data;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @Author Dwsy
// * @Date 2022/8/25
// */
//@Data
//public class ArticleContentDTO {
//    private String title;
//    private String text_md;
//    private String text_html;
//    private String text_pure;
//    private ArticleState articleState;
//    private boolean allow_comment;
//    private long view_num = 0;
//    private long collect_num = 0;
//    private String banner;
//    private String summary;
//    private Set<articleTagDTO> articleTags;
//    private articleGroupDTO articleGroup;
//
//    public static ArticleContentDTO convert(ArticleContent item) {
//        if (item == null) {
//            return null;
//        }
//        ArticleContentDTO result = new ArticleContentDTO();
//        result.setTitle(item.getTitle());
//        result.setText_md(item.getText_md());
//        result.setText_html(item.getText_html());
//        result.setText_pure(item.getText_pure());
//        result.setArticleState(item.getArticleState());
//        result.setAllow_comment(item.isAllow_comment());
//        result.setView_num(item.getView_num());
//        result.setCollect_num(item.getCollect_num());
//        result.setBanner(item.getBanner());
//        result.setArticleTags(item.getArticleTags()
//                .stream().map(articleTagDTO::convert).collect(Collectors.toSet()));
//        result.setArticleGroup(articleGroupDTO.convert(item.getArticleGroup()));
//        return result;
//    }
//}
