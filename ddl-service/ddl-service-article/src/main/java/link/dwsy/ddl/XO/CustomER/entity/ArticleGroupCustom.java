package link.dwsy.ddl.XO.CustomER.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.ArticleContent;
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
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","deleted","createTime","lastModifiedTime"})
public class ArticleGroupCustom extends BaseEntityCustom {
    private String name;

    private String info;
    @OneToMany(mappedBy = "articleGroup")
    @JsonIgnore
    private List<ArticleContent> articleContents;

    @Override
    public String toString() {
        return "ArticleGroup{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}