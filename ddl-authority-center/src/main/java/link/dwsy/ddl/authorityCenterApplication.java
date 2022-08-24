package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
//@EnableDiscoveryClient
//@EnableDiscoveryClient(autoRegister = false)
@EnableJpaAuditing
@EnableWebMvc
//@MapperScan("link.dwsy.ddl.dao")
public class authorityCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(authorityCenterApplication.class, args);
    }

}
