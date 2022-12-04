package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
//@SpringBootApplication

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@EnableJpaRepositories(basePackages = {"link.dwsy.ddl.repository"})
public class articleApplication {
    public static void main(String[] args) {
        SpringApplication.run(articleApplication.class, args);
    }

}
