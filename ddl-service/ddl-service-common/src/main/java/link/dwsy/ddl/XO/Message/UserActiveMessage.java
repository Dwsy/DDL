package link.dwsy.ddl.XO.Message;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sourceId;//aid or uid or qaid

    private UserActiveType userActiveType;

    private String ua;

    private String formContent;//己方

    private String toContent;//他方

    private boolean cancel;
}
