package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
//@SpringBootApplication
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@ConfigurationPropertiesScan
@EnableDiscoveryClient
//@EnableDiscoveryClient(autoRegister = false)
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableScheduling
public class DataApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

}
