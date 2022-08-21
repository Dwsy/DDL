package link.dwsy.ddl;

import link.dwsy.ddl.core.domain.UsernameAndPassword;
import link.dwsy.ddl.service.TokenService;
import link.dwsy.ddl.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@SpringBootTest
public class JwtTokenTest {
    @Resource
    TokenServiceImpl tokenService;
    @Test
    public void generateToken() {
//        String username, String password, int expire
        try {
            System.out.println(tokenService.generateToken("dwsy", "111"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void registered() throws Exception {
//        1重复
        UsernameAndPassword uap = new UsernameAndPassword();
        uap.setUsername("dwsy");
        uap.setPassword("123");
        System.out.println(tokenService.generateToken("dwsy","123"));
////        2成功注册
//        UsernameAndPassword uap1 = new UsernameAndPassword();
//        uap1.setUsername("dwsy7");
//        uap1.setPassword("123");
//        System.out.println(tokenService.registerUserAndGenerateToken(uap1));
    }
}
