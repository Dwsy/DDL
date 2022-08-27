package link.dwsy.ddl.XO.CustomER.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.DTO.ArticleList;
import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Entity
@Table(name = "article_content")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","articleContent"})
public class ArticleContentCustom extends BaseEntityCustom  {
    @ManyToOne
    private UserCustom user;

    private String title;
//        @Lob
//    @Type(type = "org.hibernate.type.TextType")
//    private String textMd;
//    @Lob
//    @Type(type = "org.hibernate.type.TextType")
    private String textHtml;
//    @Lob
//    @Type(type = "org.hibernate.type.TextType")
//    private String textPure;

    private String summary;

    @Enumerated(EnumType.ORDINAL)
    private ArticleState articleState;

    private boolean allowComment;

    private long viewNum = 0;

    private long collectNum = 0;

    private String banner;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "article_tag_ref",
            joinColumns = {@JoinColumn(name = "article_content_id")},
            inverseJoinColumns = {@JoinColumn(name = "article_tag_id")})
    private Set<ArticleTagCustom> articleTags;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ArticleGroupCustom articleGroup;


    //    @Fetch(FetchMode.JOIN)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "articleContent")
    @JsonIgnore
    private Set<ArticleCommentCustom> articleComments;

    @Override
    public String toString() {
        return "ArticleContentCustom{" +
                "user=" + user +
                ", title='" + title + '\'' +
                ", textHtml='" + textHtml + '\'' +
                ", summary='" + summary + '\'' +
                ", articleState=" + articleState +
                ", allowComment=" + allowComment +
                ", viewNum=" + viewNum +
                ", collectNum=" + collectNum +
                ", banner='" + banner + '\'' +
                ", articleTags=" + articleTags +
                ", articleGroup=" + articleGroup +
                '}';
    }
}
