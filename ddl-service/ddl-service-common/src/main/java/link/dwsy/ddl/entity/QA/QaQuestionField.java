package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/28
 */


@Entity
@Table(name = "qa_question_field")
@Getter
@Setter
@Builder()
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted"})
public class QaQuestionField extends BaseEntity {

    @Builder.Default
    boolean allowAnswer = true;
    @Builder.Default
    int answerNum = 0;
    @Builder.Default
    int viewNum = 0;
    @Builder.Default
    int collectNum = 0;
    @Builder.Default
    int upNum = 0;
    @Builder.Default
    int downNum = 0;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String title;
    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    private QuestionState questionState = QuestionState.ASK;
    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private QuestionState beforeQuestionState = null;
    private String summary;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "article_content_id")
    @JsonIgnore
    private QaQuestionContent qaQuestionContent;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "qa_tag_ref",
            joinColumns = {@JoinColumn(name = "qa_field_id")},
            inverseJoinColumns = {@JoinColumn(name = "qa_tag_id")})
    private List<QaTag> questionTags;

    @ManyToOne()
    @JsonProperty(value = "group")
    private QaGroup qaGroup;

    private Integer watchNum;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionField")
    @JsonIgnore
//    @Fetch(FetchMode.SUBSELECT)
    private List<QaAnswer> qaAnswers;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CodeHighlightStyle codeHighlightStyle = CodeHighlightStyle.xcode;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MarkDownTheme markDownTheme = MarkDownTheme.cyanosis;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CodeHighlightStyle codeHighlightStyleDark = CodeHighlightStyle.githubDark;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MarkDownThemeDark markDownThemeDark = MarkDownThemeDark.geekBlackDark;

    @Transient
    private List<QaAnswer> questionCommentList;

    @Transient
    private long questionCommentNum;

    @Transient
    private long questionCommentTotalPages;

    @Builder.Default
    private Integer version = -1;
}
