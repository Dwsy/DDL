package link.dwsy.ddl.entity.QA;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @Author Dwsy
 * @Date 2022/8/28
 */


@Entity
@Table(name = "qa_question_content")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "createTime", "deleted", "lastModifiedTime"})
public class QaQuestionContent extends BaseEntity {

    @Column(name = "qa_question_field_id")
    private long questionFieldId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textMd;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textHtml;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textPure;


}
