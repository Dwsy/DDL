package link.dwsy.ddl.XO.RB.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

@Data
@Builder
public class ChannelMessageVo {

    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userid;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long channel;
}
