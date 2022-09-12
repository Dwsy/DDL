package link.dwsy.ddl.XO.VO.Notify;

import link.dwsy.ddl.XO.Enum.NotifyType;
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
    private long fromUserId;
    private long toUserId;
    private long articleId;
    private long commentId;
    private NotifyType notifyType;
    private String formContent;//己方
    private String toContent;//他方

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
