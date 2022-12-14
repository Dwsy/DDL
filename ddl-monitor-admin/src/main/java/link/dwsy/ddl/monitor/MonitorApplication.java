package link.dwsy.ddl.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 监控中心
 *
 * @author ruoyi
 */
@EnableAdminServer
@SpringBootApplication
@ConfigurationPropertiesScan
public class MonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        System.out.println("监控中心启动成功");

    }
}
