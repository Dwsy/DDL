package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.Article.ArticleField;
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
@Table(name = "qa_group")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","createTime","deleted","lastModifiedTime"})
public class QaGroup extends BaseEntity {
    private String name;

    private String info;
    @OneToMany(mappedBy = "qaGroup")
    @JsonIgnore
    private List<QaQuestionField> qaQuestionFields;

    @Override
    public String toString() {
        return "ArticleGroup{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}

