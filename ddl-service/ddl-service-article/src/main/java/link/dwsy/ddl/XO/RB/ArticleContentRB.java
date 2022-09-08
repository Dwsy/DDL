package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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

    private Set<Long> articleTagIds;
    @NotBlank(message = "分组不能为空")
    private Long articleGroupId;

    private Long articleId;
}
