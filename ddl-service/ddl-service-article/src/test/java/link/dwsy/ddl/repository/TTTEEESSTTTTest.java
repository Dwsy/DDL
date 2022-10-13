package link.dwsy.ddl.repository;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @Author Dwsy
 * @Date 2022/10/12
 */

public class TTTEEESSTTTTest {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Snowflake snowflake = IdUtil.getSnowflake(31, 0);
        long id = snowflake.nextId();
        System.out.println(id);

//简单使用
//        long id = IdUtil.getSnowflakeNextId();
//        String id = snowflake.getSnowflakeNextIdStr();
    }
}
