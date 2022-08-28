package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "qa_tag")
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","createTime","deleted","lastModifiedTime"})
public class QaTag extends BaseEntity {


    private String name;

    private int questionNum = 0;

    private String tagInfo;


    @ManyToMany(mappedBy = "questionTags",
                fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QaQuestionField> qaQuestionFields;

    public QaTag() {

    }

    @Override
    public String toString() {
        return "ArticleTag{" +
                "name='" + name + '\'' +
                ", questionNum=" + questionNum +
                ", tag_info='" + tagInfo + '\'' +
                '}';
    }
}
