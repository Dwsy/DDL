package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
//@EnableDiscoveryClient
//@EnableDiscoveryClient(autoRegister = false)
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class searchApplication {
    public static void main(String[] args) {
        SpringApplication.run(searchApplication.class, args);
    }
}
