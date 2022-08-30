package link.dwsy.ddl.entity.Article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

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
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","createTime","deleted","lastModifiedTime"})
public class ArticleGroup extends BaseEntity {
    private String name;

    private String info;
    @OneToMany(mappedBy = "articleGroup")
    @JsonIgnore
    private List<ArticleField> articleFields;

    @Builder.Default
    private int articleNum=0;

    @Override
    public String toString() {
        return "ArticleGroup{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}

