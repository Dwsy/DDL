package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Entity
@Table(name = "qa_answer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer"})
public class QaAnswer extends BaseEntity {

//    @ManyToOne
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private QaQuestionField questionField;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
//  回答可能需要贴代码高亮显示用md2html保存
    private String textHtml;

    @Builder.Default
    int upNum = 0;
    @Builder.Default
    int downNum = 0;


    @Column(name = "parent_user_id")
    private long parentUserId;

    @Builder.Default
    private long parentAnswerId=0;

    @Transient
    private Set<QaAnswer> childQaAnswers;

    @Enumerated(EnumType.ORDINAL)
    private CommentType commentType;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String ua;

    @Override
    public String toString() {
        return "ArticleComment{" +
                "user=" + user +
                ", textHtml='" + textHtml + '\'' +
                ", upNum=" + upNum +
                ", commentType=" + commentType +
                ", ua='" + ua + '\'' +
                '}';
    }
}
