package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
public class ArticleRecoveryRB {
    @NotEmpty(message = "文章ID不能为空")
    private List<Long> aids;
}
