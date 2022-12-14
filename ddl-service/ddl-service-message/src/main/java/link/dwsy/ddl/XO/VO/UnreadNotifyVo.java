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

    private int unreadNotifyCount;

    private int unreadNotifyReplyCommentCount;

    private int unreadNotifyArticleOrCommentThumbCount;

    private int unreadNotifyQuestionOrAnswerThumbCount;

    private int unreadNotifyAnswerCount;

    private int unreadNotifyAnswerCommentCount;

    private int unreadNotifyQuestionCommentCount;

    private int unreadPrivateMessageCount;

    private int unreadSystemMessageCount;

    private int unreadAtMeCount;

    private int unreadInvitationAnswerCount;

    private int unreadAcceptedAnswerCount;

    private int unreadWatchAnswer;

    private int unreadWatchAcceptedQuestionAnswer;
    private int unreadTweetThumb;
    private int unreadTweetComment;
}
