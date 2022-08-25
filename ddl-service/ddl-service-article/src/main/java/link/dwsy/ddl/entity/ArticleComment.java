package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.CommentType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ArticleComment extends BaseEntity{

    @OneToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ArticleContent articleContent;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    private long thumb;

//    @OneToOne
//    @JsonIgnore
    private long parentCommentId;

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
