package link.dwsy.ddl.repository;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/10/12
 */
@SpringBootTest
public class TTTEEESSTTTTest {
    @Resource
    private ArticleDailyDataRepository articleDailyDataRepository;

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
        List<DailyData_Id_And_ScoreCount> daysRank = articleDailyDataRepository.getNDaysRank(LocalDate.now().minusDays(3), 10);
        for (DailyData_Id_And_ScoreCount articleDailyData : daysRank) {
            System.out.println("id" + articleDailyData.getId());
            System.out.println("score" + articleDailyData.getScoreCount());
        }
    }

}
