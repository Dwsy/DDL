package link.dwsy.ddl;
import link.dwsy.ddl.annotation.authAnnotation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@SpringBootTest
public class authTest {

    @Test
    @authAnnotation
    public void t() {
        System.out.println("test");
    }
}
