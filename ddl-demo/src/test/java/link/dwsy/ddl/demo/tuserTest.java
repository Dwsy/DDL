package link.dwsy.ddl.demo;

import link.dwsy.ddl.demo.entity.tuser;
import link.dwsy.ddl.demo.mapper.tuserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@SpringBootTest
public class tuserTest {
    @Resource
    private tuserMapper tmp;

    @Test
    public void test1(){
        tmp.selectList(null).forEach(System.out::println);
        tuser t = new tuser();
        t.setUsername("dwsy");
        t.setExtraInfo("oo");
        t.setPassword("111");
        tmp.insert(t);

    }

}
