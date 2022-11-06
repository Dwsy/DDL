package link.dwsy.ddl.entity.Data.Question;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "question_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class QuestionDailyData extends BaseEntity {
    private long questionFieldId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    String title;

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

}
