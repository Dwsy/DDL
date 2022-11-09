package link.dwsy.ddl.XO.Message.Question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/11/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationUserAnswerQuestionMsg {

    private long formUserId;

    private long toUserId;

    private long questionId;

    private String answerTitle;

    private boolean cancel;

}
