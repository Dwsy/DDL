package link.dwsy.ddl.entity.Article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "createTime", "deleted", "lastModifiedTime","indexPageDisplay"})
public class ArticleTag extends BaseEntity {


    private String name;

    @Builder.Default
    private int articleNum = 0;

    //排序权重
    private Integer weight;

    //是否在主页显示
    @Builder.Default
    private boolean indexPageDisplay = false;

    private String tagInfo;


    @ManyToMany(mappedBy = "articleTags",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ArticleField> articleFields;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private ArticleGroup articleGroup;

    public ArticleTag() {

    }

    @Override
    public String toString() {
        return "ArticleTag{" +
                "name='" + name + '\'' +
                ", articleNum=" + articleNum +
                ", tag_info='" + tagInfo + '\'' +
                '}';
    }
}
