package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/10/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuestionRB {

//    @NotBlank(message = "问题标题不能为空")
    @Size(min = 1, max = 255, message = "问题标题长度为1-255")
    private String title;

    @NotBlank(message = "问题内容不能为空")
//  Markdown
    private String content;

    @Size(min = 0, max = 255, message = "概述长度为0-255")
    private String summary;

    @Builder.Default
    private QuestionState questionState = QuestionState.ASK;

    @Builder.Default
    private boolean allow_answer = true;


    @NotEmpty(message = "标签不能为空")
    private List<Long> questionTagIds;

    @NotNull(message = "分组不能为空")
    private Long questionGroupId;

    private Long questionId;//change

    @Builder.Default
    private CodeHighlightStyle codeHighlightStyle = CodeHighlightStyle.vs2015;

    @Builder.Default
    private MarkDownTheme markDownTheme = MarkDownTheme.cyanosis;

    @Builder.Default
    private CodeHighlightStyle codeHighlightStyleDark = CodeHighlightStyle.githubDark;

    @Builder.Default
    private MarkDownThemeDark markDownThemeDark = MarkDownThemeDark.geekBlackDark;

    private boolean sendInfinity;

}
