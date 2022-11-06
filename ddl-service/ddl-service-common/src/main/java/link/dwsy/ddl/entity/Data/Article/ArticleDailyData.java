package link.dwsy.ddl.entity.Data.Article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "article_daily_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer"})
public class ArticleDailyData extends BaseEntity {
    private long articleFieldId;

    @Builder.Default
    private int upNum = 0;

    @Builder.Default
    private int downNum = 0;

    @ManyToOne
    private User user;

    private String title;

    private String summary;


    @Builder.Default
    private int commentNum = 0;

    @Builder.Default
    private int viewNum = 0;

    @Builder.Default
    private int collectNum = 0;

    private String banner;

}
