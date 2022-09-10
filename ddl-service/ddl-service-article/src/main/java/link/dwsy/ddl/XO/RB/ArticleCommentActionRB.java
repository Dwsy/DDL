package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */
@Data
public class ArticleCommentActionRB {
    private CommentType commentType;
    @NotNull(message = "文章ID不能为空")
    private long ArticleFieldId;

    private long actionUserId;

    private long actionCommentId;
}
