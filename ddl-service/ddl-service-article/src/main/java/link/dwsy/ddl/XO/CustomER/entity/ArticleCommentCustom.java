package link.dwsy.ddl.XO.CustomER.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.CommentType;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Entity
@Table(name = "article_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","lastModifiedTime","articleContent"})
public class ArticleCommentCustom extends BaseEntityCustom implements Serializable {

    @OneToOne
    private UserCustom  user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArticleContent articleContent;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    private long thumb;

//    @OneToOne
//    @JsonIgnore
    private long parentCommentId;

    @Transient
    private Set<ArticleCommentCustom> childComments;
    @Enumerated(EnumType.ORDINAL)
    private CommentType commentType;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String ua;

    @Override
    public String toString() {
        return "ArticleComment{" +
                "user=" + user +
                ", text='" + text + '\'' +
                ", thumb=" + thumb +
                ", commentType=" + commentType +
                ", ua='" + ua + '\'' +
                '}';
    }
}
