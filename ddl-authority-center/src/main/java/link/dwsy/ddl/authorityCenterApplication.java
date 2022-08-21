package link.dwsy.ddl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@SpringBootApplication(scanBasePackages = {"link.dwsy.ddl"})
@EnableDiscoveryClient
@MapperScan("link.dwsy.ddl.dao")
public class authorityCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(authorityCenterApplication.class, args);
    }

}
