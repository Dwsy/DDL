package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import lombok.*;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserActiveMessage {
    private Long userId;

    private Long sourceId;//aid or uid or qaid

    private UserActiveType userActiveType;

    private String ua;

    private String formContent;//己方

    private String toContent;//他方

    private boolean cancel;
}
