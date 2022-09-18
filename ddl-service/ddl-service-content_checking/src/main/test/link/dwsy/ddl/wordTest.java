package link.dwsy.ddl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import link.dwsy.ddl.entity.ContentChecking.WordEntity;
import link.dwsy.ddl.XO.Enum.WordType;
import link.dwsy.ddl.repository.ContentChecking.WordReptitory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class wordTest {

    @Resource
    WordReptitory wordReptitory;

    @Resource
    SensitiveWordBs sensitiveWordBs;

    @Test
    public void addWord() {
        wordReptitory.save(WordEntity.builder()
                .type(WordType.Deny)
                .word("sb")
                .build());
        wordReptitory.save(WordEntity.builder()
                .type(WordType.Deny)
                .word("nmsl")
                .build());
        wordReptitory.save(WordEntity.builder()
                .type(WordType.Deny)
                .word("nt")
                .build());
        wordReptitory.save(WordEntity.builder()
                .type(WordType.Deny)
                .word("傻逼")
                .build());
    }

}
