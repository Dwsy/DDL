package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author Dwsy
 * @Date 2022/9/3
 */
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
//@EnableAspectJAutoProxy
public class contentCheckingApplication {
    public static void main(String[] args) {
        SpringApplication.run(contentCheckingApplication.class, args);
    }
}
