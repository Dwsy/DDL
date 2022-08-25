package link.dwsy.ddl.XO.DTO;

import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.entity.ArticleGroup;
import link.dwsy.ddl.entity.ArticleTag;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
