package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.xo.Enum.ArticleState;
import lombok.*;

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
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ArticleContent extends BaseEntity {
    private String title;

    private String text_md;

    private String text_html;

    private String text_pure;

    @Enumerated(EnumType.ORDINAL)
    private ArticleState articleState;

    private boolean allow_comment;

    private long view_num = 0;

    private long collect_num = 0;

    private String banner;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "article_tag_ref",
            joinColumns = { @JoinColumn(name = "article_content_id") },
            inverseJoinColumns = { @JoinColumn(name = "article_tag_id") })
    private Set<ArticleTag> articleTags;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ArticleGroup articleGroup;
}
