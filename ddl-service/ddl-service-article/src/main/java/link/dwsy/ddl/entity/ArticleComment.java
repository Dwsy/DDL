package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.xo.Enum.CommentType;
import lombok.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ArticleContent articleContent;

    private String text;

    private long thumb;

    @OneToOne
    @JsonIgnore
    private ArticleComment parentComment;

    @Enumerated(EnumType.ORDINAL)
    private CommentType commentType;

    @Override
    public String toString() {
        return "ArticleComment{" +
                "user=" + user +
                ", text='" + text + '\'' +
                ", thumb=" + thumb +
                ", commentType=" + commentType +
                '}';
    }
}
