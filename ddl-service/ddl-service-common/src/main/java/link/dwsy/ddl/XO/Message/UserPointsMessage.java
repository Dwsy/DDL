package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.User.PointsType;
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
public class UserPointsMessage {
    private Long userId;

    private PointsType pointsType;
}
