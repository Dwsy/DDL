package link.dwsy.ddl.XO.CustomER.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

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
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","deleted","createTime","lastModifiedTime"})
public class ArticleTagCustom extends BaseEntityCustom {


    private String name;

    private Long articleNum = 0L;

    private String tagInfo;


    @ManyToMany(mappedBy = "articleTags",
                fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ArticleContent> articleContents;



    @Override
    public String toString() {
        return "ArticleTag{" +
                "name='" + name + '\'' +
                ", articleNum=" + articleNum +
                ", tag_info='" + tagInfo + '\'' +
                '}';
    }
}
