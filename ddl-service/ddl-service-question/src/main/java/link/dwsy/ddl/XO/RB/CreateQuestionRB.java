package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message = "问题标题不能为空")
    @Max(value = 249)
    private String title;

    @NotBlank(message = "问题内容不能为空")
//  Markdown
    private String content;

    @Max(value = 249)
    private String summary;

    @Builder.Default
    private QuestionState questionState = QuestionState.ASK;

    @Builder.Default
    private boolean allow_answer = true;

//    private String banner;

    @NotEmpty(message = "标签不能为空")
    private List<Long> questionTagIds;

    @NotNull(message = "分组不能为空")
    private Long questionGroupId;

//    @NotNull(message = "文章来源不能为空")
//    private ArticleSource articleSource;

//    private String articleSourceUrl;

    private Long questionId;//change

    @Builder.Default
    private CodeHighlightStyle codeHighlightStyle = CodeHighlightStyle.vs2015;

    @Builder.Default
    private MarkDownTheme markDownTheme = MarkDownTheme.cyanosis;

    @Builder.Default
    private CodeHighlightStyle codeHighlightStyleDark = CodeHighlightStyle.githubDark;

    @Builder.Default
    private MarkDownThemeDark markDownThemeDark = MarkDownThemeDark.geekBlackDark;

}
