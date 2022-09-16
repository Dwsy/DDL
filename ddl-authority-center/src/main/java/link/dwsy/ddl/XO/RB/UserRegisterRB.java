package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "密码不能为空")
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
