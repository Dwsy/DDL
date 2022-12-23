package link.dwsy.ddl.entity.Article;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.ArticleSource;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.List;

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
@JsonIgnoreProperties(value = {"deleted"})
@Proxy(lazy = false)
public class ArticleField extends BaseEntity {

    @Builder.Default
    int upNum = 0;
    @Builder.Default
    int downNum = 0;
    @ManyToOne
    private User user;
    private String title;
    private String summary;
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private ArticleState articleState = ArticleState.published;
    @Builder.Default
    private boolean allowComment = true;
    @Builder.Default
    private int commentNum = 0;


    @Builder.Default
    private int viewNum = 0;

    @Builder.Default
    private int collectNum = 0;

    private String banner;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MarkDownTheme markDownTheme = MarkDownTheme.cyanosis;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MarkDownThemeDark markDownThemeDark = MarkDownThemeDark.ayuMirage;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CodeHighlightStyle codeHighlightStyle = CodeHighlightStyle.xcode;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CodeHighlightStyle codeHighlightStyleDark = CodeHighlightStyle.xcode;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "article_content_id")
    @JsonIgnore
    @JSONField
    private ArticleContent articleContent;

    private ArticleSource articleSource;

    private String articleSourceUrl;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "article_tag_ref",
            joinColumns = {@JoinColumn(name = "article_field_id")},
            inverseJoinColumns = {@JoinColumn(name = "article_tag_id")})
    @Fetch(FetchMode.SUBSELECT)
    private List<ArticleTag> articleTags;

    @ManyToOne()
    private ArticleGroup articleGroup;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "articleField")
    @JsonIgnore
//    @Fetch(FetchMode.SUBSELECT)
    private List<ArticleComment> articleComments;

    @Builder.Default
    private Integer version = -1;

}
