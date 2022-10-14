package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.ArticleSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleContentRB {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String summary;

    @Builder.Default
    private ArticleState articleState = ArticleState.open;

    @Builder.Default
    private boolean allowComment = true;

    private String banner;

    @NotEmpty(message = "标签不能为空")
    private Set<Long> articleTagIds;

    @NotNull(message = "分组不能为空")
    private Long articleGroupId;

    @NotNull(message = "文章来源不能为空")
    private ArticleSource articleSource;

    private String articleSourceUrl;

    private Long articleId;//change
}
