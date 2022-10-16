package link.dwsy.ddl.XO.VO.Notify;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.entity.User.UserNotify;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/9/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentNotifyVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long fromUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long toUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long articleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long commentId;

    private NotifyType notifyType;

    private String formContent;//己方

    private String toContent;//他方

    //点赞 回复
    public CommentNotifyVO(UserNotify userNotify) {
        this.fromUserId = userNotify.getFromUserId();
        this.toUserId = userNotify.getToUserId();
        this.articleId = userNotify.getArticleId();
        this.commentId = userNotify.getCommentId();
        this.notifyType = userNotify.getNotifyType();
        this.formContent = userNotify.getFormContent();
        this.toContent = userNotify.getToContent();
    }
}
