package link.dwsy.ddl.repository;

import link.dwsy.ddl.XO.Enum.User.Gender;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserInfo;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */
@SpringBootTest
public class userTest {
    @Resource
    UserRepository userRepository;
    @Test
    public void save() {
        UserInfo info = UserInfo.builder().avatar("avatar").gender(Gender.MAIL).sign("sign").build();
        User u = User.builder()
                .username("Dwsy")
                .password("123").level(5).userInfo(info).build();
        userRepository.save(u);
    }

    @Test
    public void update() {
        User user = userRepository.findById(1L).get();
        user.setLevel(6);
        user.getUserInfo().setSign("666");
        userRepository.save(user);
    }

    @Test
    public void remove() {
        userRepository.deleteById(2L);
    }
}
