package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/10/24
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnreadNotifyVo {

    private Integer unreadNotifyCount;

    private Integer unreadNotifyReplyCommentCount;

    private Integer unreadNotifyArticleOrCommentThumbCount;

    private Integer unreadNotifyQuestionOrAnswerThumbCount;

    private Integer unreadNotifyAnswerCount;

    private Integer unreadNotifyAnswerCommentCount;

    private Integer unreadNotifyQuestionCommentCount;

    private Integer unreadPrivateMessageCount;

    private Integer unreadSystemMessageCount;

    private Integer unreadAtMeCount;

    private Integer unreadInvitationAnswerCount;

    private Integer unreadAcceptedAnswerCount;
}
