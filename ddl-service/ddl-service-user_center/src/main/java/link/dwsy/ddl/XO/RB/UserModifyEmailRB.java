package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.Email;

/**
 * @Author Dwsy
 * @Date 2022/10/7
 */

@Data
public class UserModifyEmailRB {

    boolean captcha;

    @Email
    String email;

    String code;
}
