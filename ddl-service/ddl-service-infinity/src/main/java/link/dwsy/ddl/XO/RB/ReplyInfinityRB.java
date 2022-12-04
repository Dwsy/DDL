package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/24
 */
@Data
public class ReplyInfinityRB {
    @Min(value = 1, message = "回复的id不合法")
    private Long replyId;

    private String content;

    @Min(value = 1, message = "回复类型不合法")
    private Long replyUserId;

    @Min(value = 1, message = "回复对象id不合法")
    private Long replyUserTweetId;

    private Long refId;

    @Size(max = 9, message = "图片数量最大为9张")
    private List<String> imgUrlList;

}
