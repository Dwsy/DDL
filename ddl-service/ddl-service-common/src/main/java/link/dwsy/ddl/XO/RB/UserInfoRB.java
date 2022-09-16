package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.User.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRB {
    private String avatar;

    private String sign;

    private String nickname;
//    private String email;
//
//    private String phone;

    private Date birth;

    private Gender gender;

}
