package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.ArticleState;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Entity
@Table(name = "article_content")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","articleField"})
public class ArticleContent extends BaseEntity {

    //    @OneToOne(mappedBy = "articleContent", fetch = FetchType.LAZY)
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "article_field_id",referencedColumnName = "id")
    @Column(name = "article_field_id")
    private long articleFieldId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textMd;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textHtml;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String textPure;

//    @Column(name = "article_field_id")
//    private Long articleFieldId;
}
