package link.dwsy.ddl;

import link.dwsy.ddl.XO.Enum.User.Gender;
import link.dwsy.ddl.XO.RB.UserInfoRB;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Optional;

@SpringBootTest
public class userInfoTest {

    @Resource
    UserRepository userRepository;
    @Test
    public void T1() {
        var userInfo = UserInfoRB.builder()
                .avatar("")
                .gender(Gender.MALE)
                .build();

        Long id = 3L;
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        Optional.ofNullable(userInfo.getAvatar()).ifPresent(user.getUserInfo()::setAvatar);
        Optional.ofNullable(userInfo.getSign()).ifPresent(user.getUserInfo()::setSign);
        Optional.ofNullable(userInfo.getBirth()).ifPresent(user.getUserInfo()::setBirth);
        Optional.ofNullable(userInfo.getNickname()).ifPresent(user::setNickname);
        Optional.ofNullable(userInfo.getGender()).ifPresent(user.getUserInfo()::setGender);
        System.out.println(user.getNickname());
        userRepository.save(user);
    }
}
