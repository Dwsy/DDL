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

    @NotBlank(message = "can not be empty")
    private long replyUserId;

    private long replyUserCommentId;

    @NotBlank(message = "can not be empty")
    private long parentCommentId ;

}
