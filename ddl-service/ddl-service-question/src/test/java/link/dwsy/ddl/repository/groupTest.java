package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.QA.QaGroup;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.repository.QA.QaGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/28
 */
@SpringBootTest
public class groupTest {

    @Resource
    QaGroupRepository qaGroupRepository;

    @Test
    public void addGroup() {
        QaGroup g1 = QaGroup.builder().name("前端").info("{}").build();
        QaGroup g2 = QaGroup.builder().name("中端").info("{}").build();
        QaGroup g3 = QaGroup.builder().name("后端").info("{}").build();
        List<QaGroup> qaGroups = Arrays.asList(g1, g2, g3);
        qaGroupRepository.saveAll(qaGroups);
        qaGroupRepository.findAll().forEach(System.out::println);
    }

}
