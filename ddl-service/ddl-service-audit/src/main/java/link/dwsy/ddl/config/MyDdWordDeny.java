package link.dwsy.ddl.config;


import com.github.houbb.sensitive.word.api.IWordDeny;
import link.dwsy.ddl.XO.Enmu.WordType;
import link.dwsy.ddl.entity.WordEntity;
import link.dwsy.ddl.repositor.WordRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义敏感词
 *
 * @author 老马啸西风
 * @since 1.1.0
 */
@Component
public class MyDdWordDeny implements IWordDeny {

    @Resource
    private WordRepository wordRepository;

    @Override
    public List<String> deny() {

        List<WordEntity> wordList = wordRepository.findByDeletedFalseAndType(WordType.Deny);

        return wordList.stream().map(WordEntity::getWord).collect(Collectors.toList());
    }


}


