package link.dwsy.ddl.entity.Data.Question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class QuestionDailyData {

    @Id
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private long questionFieldId;

    private long userId;

    int answerNum = 0;

    int commentNum = 0;

    int viewNum = 0;


    int collectNum = 0;


    int upNum = 0;


    int downNum = 0;

    private int score = 0;

    private LocalDate dataDate;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Long> tagIds = new ArrayList<>();
    private long groupId;

    @Transient
    private int countScore;

}
