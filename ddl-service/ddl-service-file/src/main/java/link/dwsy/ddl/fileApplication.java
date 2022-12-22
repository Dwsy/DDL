package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@ConfigurationPropertiesScan
//@EnableAspectJAutoProxy
//@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
//@EnableSwagger2WebMvc
public class fileApplication {
    public static void main(String[] args) {
        SpringApplication.run(fileApplication.class, args);
    }
}

