package link.dwsy.ddl.entity.Data.Infinity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @Author Dwsy
 * @Date 2022/12/15
 */
@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class InfinityTopicDailyData {
    @Id
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    private long InfinityTopicId;

    private long viewNum;

    private long infinityNum;

    private long followerNum;

    private long replyNum;

    private int score = 0;
    private LocalDate dataDate;
    @Transient
    private int scoreCount;
}