package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
public class QaAnswer extends BaseEntity {

    @Builder.Default
    int upNum = 0;
    @Builder.Default
    int downNum = 0;
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
    @Lob
    @Type(type = "org.hibernate.type.TextType")
//  回答可能需要贴代码高亮显示用md2html保存
    private String textMd;
//    @Transient
//    private String textPure;

    private Boolean accepted;

    private Date acceptedTime;

    @Column(name = "parent_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long parentUserId;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long parentAnswerId = 0;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long replyUserAnswerId = 0;

    private int answerSerialNumber;


    @Transient
    private List<QaAnswer> childQaAnswers;

    @Transient
    private long childQaAnswerNum;

    @Transient
    private long childQaAnswerTotalPages;

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) //返回值
    private AnswerType userAction;

    @Enumerated(EnumType.ORDINAL)
    private AnswerType answerType;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String ua;

    @Override
    public String toString() {
        return "ArticleComment{" +
                "user=" + user +
                ", textHtml='" + textHtml + '\'' +
                ", upNum=" + upNum +
                ", AnswerType=" + answerType +
                ", ua='" + ua + '\'' +
                '}';
    }
}
