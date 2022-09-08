package link.dwsy.ddl.XO.RB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class channelSendMessageRB {

    private String message;

    private Long channelId;

}
