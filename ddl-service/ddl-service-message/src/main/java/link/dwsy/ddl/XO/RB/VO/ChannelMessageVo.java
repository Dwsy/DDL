package link.dwsy.ddl.XO.RB.VO;

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
    private Long userid;
    private String message;
    private Long channel;
}
