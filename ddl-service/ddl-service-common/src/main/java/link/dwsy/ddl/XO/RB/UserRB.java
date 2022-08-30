package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Data
public class UserRB {

    private String username;

    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;

}
