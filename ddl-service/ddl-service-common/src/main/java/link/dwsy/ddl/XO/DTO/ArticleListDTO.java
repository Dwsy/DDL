package link.dwsy.ddl.XO.DTO;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import lombok.Value;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Value
//@Data
//@Builder
//@AllArgsConstructor
public class ArticleListDTO {


    String title;

//    private String textMd;


    String textHtml;

//    private String textPure;

    String summary;
    //
    ArticleState articleState;
    //
    boolean allowComment;

    long viewNum;
    //
    long collectNum;
    //
    String banner;

    ArticleGroup articleGroup;

}
