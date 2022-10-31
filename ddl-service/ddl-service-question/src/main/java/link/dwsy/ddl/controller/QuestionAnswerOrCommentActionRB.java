package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Dwsy
 * @Date 2022/10/31
 */
@Data
public class QuestionAnswerOrCommentActionRB {

    private AnswerType answerType;

    @NotNull(message = "问题ID不能为空")
    private long questionFieldId;

    private long actionUserId;//

    private long actionAnswerOrCommentId;
}
