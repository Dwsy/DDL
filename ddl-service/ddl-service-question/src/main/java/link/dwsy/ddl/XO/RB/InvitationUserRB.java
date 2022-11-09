package link.dwsy.ddl.XO.RB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * @Author Dwsy
 * @Date 2022/11/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationUserRB {

    @Range(min = 1)
    long userId;

    @Range(min = 1)
    long questionId;

    boolean cancel;

}
