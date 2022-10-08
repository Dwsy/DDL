package link.dwsy.ddl.XO.VO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/10/7
 */
@Data
@NoArgsConstructor
public class UserSettingVO {
    private String password;

    private String email;

    private String phone;
}
