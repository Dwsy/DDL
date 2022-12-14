package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @Author Dwsy
 * @Date 2022/11/22
 */
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@ConfigurationPropertiesScan
//@EnableDiscoveryClient
//@EnableDiscoveryClient(autoRegister = false)
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "link.dwsy.ddl.repository")
public class infinityApplication {
    public static void main(String[] args) {
        SpringApplication.run(infinityApplication.class, args);
    }
}
