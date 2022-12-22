package link.dwsy.ddl.repository;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
import link.dwsy.ddl.service.RPC.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/10/12
 */
@SpringBootTest
public class TTTEEESSTTTTest {
    @Resource
    private ArticleDailyDataRepository articleDailyDataRepository;

    @Resource
    private AuditService auditService;

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Snowflake snowflake = IdUtil.getSnowflake(31, 0);
        long id = snowflake.nextId();
        System.out.println(id);

//简单使用
//        long id = IdUtil.getSnowflakeNextId();
//        String id = snowflake.getSnowflakeNextIdStr();
    }

    @Test
    public void test1() {
        System.out.println(auditService.contains("1"));
        System.out.println(auditService.contains("nmsl"));
        System.out.println(auditService.contains("nt"));
    }

}
