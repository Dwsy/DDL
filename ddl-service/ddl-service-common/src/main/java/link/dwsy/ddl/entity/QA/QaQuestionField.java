package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime"})
public class QaQuestionField extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    String title;

    private String summary;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    QuestionState questionState=QuestionState.ASK;

    @Builder.Default
    boolean allow_answer=true;
    @Builder.Default
    int answerNum = 0;

    @Builder.Default
    int views = 0;

    @Builder.Default
    int collectNum = 0;

    @Builder.Default
    int upNum=0;

    @Builder.Default
    int downNum = 0;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            orphanRemoval = true,fetch = FetchType.LAZY,optional = true)
//    @JoinColumn(name = "article_content_id")
    @JsonIgnore
    private QaQuestionContent qaQuestionContent;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "qa_tag_ref",
            joinColumns = {@JoinColumn(name = "qa_field_id")},
            inverseJoinColumns = {@JoinColumn(name = "qa_tag_id")})
    private Set<QaTag> questionTags;

    @ManyToOne()
    @JsonProperty(value = "group")
    private QaGroup qaGroup;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "questionField")
    @JsonIgnore
//    @Fetch(FetchMode.SUBSELECT)
    private List<QaAnswer> qaAnswers;


}
