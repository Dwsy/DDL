package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
@Builder
public class UserActiveMessage {
    private Long userId;
    private Long sourceId;
    private UserActiveType userActiveType;
}
