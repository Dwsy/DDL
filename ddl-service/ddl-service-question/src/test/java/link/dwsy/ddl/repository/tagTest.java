package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.repository.QA.QaQuestionTagRepository;
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
public class tagTest {

    @Resource
    QaQuestionTagRepository qaQuestionTagRepository;

    @Test
    public void addTag() {
        QaTag java = QaTag.builder().tagInfo("{}").name("Java").build();
        QaTag rust = QaTag.builder().tagInfo("{}").name("rust").build();
        QaTag go = QaTag.builder().tagInfo("{}").name("go").build();
        QaTag cpp = QaTag.builder().tagInfo("{}").name("C++").build();
        List<QaTag> qaTags = Arrays.asList(java, rust, go, cpp);
        qaQuestionTagRepository.saveAll(qaTags);
        qaQuestionTagRepository.findAll().forEach(System.out::println);
    }


}
