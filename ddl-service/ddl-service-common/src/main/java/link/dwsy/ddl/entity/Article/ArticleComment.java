package link.dwsy.ddl.entity.Article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;

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
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
public class ArticleComment extends BaseEntity {

    //    @ManyToOne
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) //返回值
    private CommentType userAction;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ArticleField articleField;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    @Builder.Default
    int upNum = 0;

    @Builder.Default
    int downNum = 0;


    @Column(name = "parent_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long parentUserId;
//    todo 增加字段 待修改

    @Builder.Default // 0 为一级评论 -1 up or down 文章 1楼上面 -1 是内容好像没啥问题。。
    private long parentCommentId = 0;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long replyUserCommentId = 0;

    private int commentSerialNumber;

    @Transient
    private User parentUser;

    @Transient
    private Collection<ArticleComment> childComments;

    @Transient
    private long childCommentNum;

    @Transient
    private long childCommentTotalPages;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    @JsonIgnore
    private CommentType commentType = CommentType.comment;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String ua;


    @Override
    public String toString() {
        return "ArticleComment{" +
                "user=" + user +
                ", text='" + text + '\'' +
                ", upNum=" + upNum +
                ", commentType=" + commentType +
                ", ua='" + ua + '\'' +
                '}';
    }
}
