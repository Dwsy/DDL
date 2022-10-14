package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@ConfigurationPropertiesScan
//@EnableAspectJAutoProxy
//@EnableJpaAuditing
@EnableSwagger2WebMvc
public class fileApplication {
    public static void main(String[] args) {
        SpringApplication.run(fileApplication.class, args);
    }
}

