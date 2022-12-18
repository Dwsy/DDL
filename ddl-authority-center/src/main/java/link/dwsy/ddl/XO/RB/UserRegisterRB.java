package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRB {

    private String username;

    private String phone;
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    private String email;

    @Builder.Default
    private String area = "+86";

    @Builder.Default
    private String avatar = "default";

    private String sign;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birth;
}
