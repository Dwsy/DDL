package link.dwsy.ddl.XO.RB;

import lombok.Data;

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

    @NotBlank(message = "can not be empty")
    private long replyUserId;

    private long replyUserAnswerId;

    @NotBlank(message = "can not be empty")
    private long parentAnswerId;

}
