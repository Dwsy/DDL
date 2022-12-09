package link.dwsy.ddl.entity.Data.Question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "question_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class QuestionDailyData {

    @Id
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private long questionFieldId;

    @Builder.Default
    int answerNum = 0;
    @Builder.Default
    int commentNum = 0;

    @Builder.Default
    int viewNum = 0;

    @Builder.Default
    int collectNum = 0;

    @Builder.Default
    int upNum = 0;

    @Builder.Default
    int downNum = 0;

    private LocalDate date;

}
