package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.XO.Enum.User.Gender;
import link.dwsy.ddl.entity.User.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/12/8
 */
@Getter
@Setter
public class UserInfoVo {

    private int level;

    private int experience;

    private String avatar = "default";
    private String sign;

    private Gender gender;
    private Date birth;

    private boolean checkIn;


    public UserInfoVo(User user,boolean checkIn) {
        this.level = user.getLevel();
        this.experience = user.getExperience();
        this.avatar = user.getUserInfo().getAvatar();
        this.sign = user.getUserInfo().getSign();
        this.gender = user.getUserInfo().getGender();
        this.birth = user.getUserInfo().getBirth();
        this.checkIn = checkIn;
    }
}
