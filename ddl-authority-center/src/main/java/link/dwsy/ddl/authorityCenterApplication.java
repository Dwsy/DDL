package link.dwsy.ddl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
//@EnableDiscoveryClient
//@EnableDiscoveryClient(autoRegister = false)
@EnableJpaAuditing
@RestController
@RequestMapping()
//@MapperScan("link.dwsy.ddl.dao")
public class authorityCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(authorityCenterApplication.class, args);
    }

    @GetMapping()
    public String index() {
        return "Hello World";
    }

}
