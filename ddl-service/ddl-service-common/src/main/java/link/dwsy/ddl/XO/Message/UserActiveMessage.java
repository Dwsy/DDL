package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActiveMessage {
    private Long userId;
    private Long sourceId;
    private UserActiveType userActiveType;
}
