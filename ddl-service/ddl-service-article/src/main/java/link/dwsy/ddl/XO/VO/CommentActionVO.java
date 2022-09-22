package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/9/22
 */

@Data
@Builder
public class CommentActionVO {
    private CommentType commentType;

    private Long upNum;

    private Long downNum;
}
