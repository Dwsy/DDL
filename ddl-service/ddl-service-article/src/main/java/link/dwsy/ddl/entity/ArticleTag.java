package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.xo.Enum.ArticleState;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Entity
@Table(name = "article_tag")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class ArticleTag extends BaseEntity {


    private String name;

    private Long articleNum = 0L;

    private String tag_info;


    @ManyToMany(mappedBy = "articleTags",
            fetch = FetchType.LAZY)
    private Set<ArticleContent> articleContents;

    @Override
    public String toString() {
        return "ArticleTag{" +
                "name='" + name + '\'' +
                ", articleNum=" + articleNum +
                ", tag_info='" + tag_info + '\'' +
                '}';
    }
}
