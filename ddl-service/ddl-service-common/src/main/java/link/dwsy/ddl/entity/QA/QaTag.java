package link.dwsy.ddl.entity.QA;

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
@Table(name = "qa_tag")
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","createTime","indexPageDisplay","deleted","lastModifiedTime"})
public class QaTag extends BaseEntity {


    private String name;

    @Builder.Default
    private int questionNum = 0;

    //排序权重
    private Integer weight;

    //是否在主页显示

    @Builder.Default
    private boolean indexPageDisplay = false;

    private String tagInfo;


    @ManyToMany(mappedBy = "questionTags",
                fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QaQuestionField> qaQuestionFields;

    @ManyToOne
    private QaGroup qaGroup;

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
