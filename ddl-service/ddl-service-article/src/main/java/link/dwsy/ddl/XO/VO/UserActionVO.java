package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import lombok.*;

/**
 * @Author Dwsy
 * @Date 2022/9/25
 */

@Data
@NoArgsConstructor
public class UserActionVO {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER) //返回值
    private CommentType thumb;

    private boolean collect;

    private boolean follow;

}
