package link.dwsy.ddl.entity.Data.Article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class ArticleDailyData {

    @Id
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    private long articleFieldId;


    private int upNum = 0;


    private int downNum = 0;


    private int commentNum = 0;


    private int viewNum = 0;


    private int collectNum = 0;

    private int score = 0;


    private LocalDate date;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Long> tagIds = new ArrayList<>();

}
