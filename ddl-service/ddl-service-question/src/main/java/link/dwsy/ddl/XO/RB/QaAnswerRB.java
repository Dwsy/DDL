package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author Dwsy
 * @Date 2022/10/26
 */
@Data
public class QaAnswerRB {

    @NotNull(message = "问题ID不能为空")
    private long QuestionId;

    @NotBlank(message = "回答或评论内容不能为空")
    //md
    private String mdText;

    private long replyUserId;

    private long replyUserAnswerId;

    @Min(value = -1, message = "min")
    private long parentAnswerId;

    @NotNull(message = "can not be empty")
    private AnswerType answerType;
}
