package link.dwsy.ddl.config;


import com.github.houbb.sensitive.word.api.IWordAllow;
import link.dwsy.ddl.entity.ContentChecking.WordEntity;
import link.dwsy.ddl.XO.Enum.WordType;
import link.dwsy.ddl.repository.ContentChecking.WordRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class MyDdWordAllow implements IWordAllow {

    @Resource
    private WordRepository wordRepository;

    @Override
    public List<String> allow() {

        List<WordEntity> wordList = wordRepository.findByDeletedFalseAndType(WordType.Allow);

        return wordList.stream().map(WordEntity::getWord).collect(Collectors.toList());
    }

}