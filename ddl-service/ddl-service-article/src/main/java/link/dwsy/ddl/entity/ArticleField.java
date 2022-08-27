package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "article_field")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted"})
public class ArticleField extends BaseEntity {
    @ManyToOne
    private User user;

    private String title;

    private String summary;

    @Enumerated(EnumType.ORDINAL)
    private ArticleState articleState=ArticleState.open;

    private boolean allowComment=true;

    private long viewNum = 0;

    private long collectNum = 0;

    private String banner;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            orphanRemoval = true,fetch = FetchType.LAZY,optional = true)
//    @JoinColumn(name = "article_content_id")
    @JsonIgnore
    private ArticleContent articleContent;


    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "article_tag_ref",
            joinColumns = {@JoinColumn(name = "article_field_id")},
            inverseJoinColumns = {@JoinColumn(name = "article_tag_id")})
    private Set<ArticleTag> articleTags;

    @ManyToOne()
    private ArticleGroup articleGroup;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "articleField")
    @JsonIgnore
//    @Fetch(FetchMode.SUBSELECT)
    private List<ArticleComment> articleComments;


}
