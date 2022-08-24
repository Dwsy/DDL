package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Entity
@Table(name = "article_group")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ArticleGroup extends BaseEntity {
    private String name;



    private String info;
    @OneToMany(mappedBy = "articleGroup")
    private List<ArticleContent> articleContentList;

    @Override
    public String toString() {
        return "ArticleGroup{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}