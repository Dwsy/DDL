package link.dwsy.ddl.entity.Data.Article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "article_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class ArticleDailyData  {

    @Id
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    private long articleFieldId;

    @Builder.Default
    private int upNum = 0;

    @Builder.Default
    private int downNum = 0;

//    @ManyToOne
//    private User user;

//    private String title;

//    private String summary;


    @Builder.Default
    private int commentNum = 0;

    @Builder.Default
    private int viewNum = 0;

    @Builder.Default
    private int collectNum = 0;

    private LocalDate date;

//    private String banner;

}
