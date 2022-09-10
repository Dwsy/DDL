package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
public class ArticleCommentRB {
    @NotNull(message = "文章ID不能为空")
    private long ArticleFieldId;

    @NotBlank(message = "评论内容不能为空")
    private String text;

    private long parentUserId;

    private long parentCommentId ;
}
