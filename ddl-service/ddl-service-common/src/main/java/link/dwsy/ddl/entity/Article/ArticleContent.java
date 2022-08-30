package link.dwsy.ddl.entity.Article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Entity
@Table(name = "article_content")
@Getter
@Setter
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
