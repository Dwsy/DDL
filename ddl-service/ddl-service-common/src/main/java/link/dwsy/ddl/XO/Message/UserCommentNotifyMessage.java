package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentNotifyMessage {
    private Long formUserId;
//    to userid

    private Long articleId;//aid or uid or qaid

    private Long commentId;//

    private UserActiveType userActiveType;

    private String ua;

    private String formContent;//己方

    private String toContent;//他方

    private boolean cancel;
}
