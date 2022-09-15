package link.dwsy.ddl.config;


import com.github.houbb.sensitive.word.api.IWordAllow;
import link.dwsy.ddl.entity.ContentChecking.WordEntity;
import link.dwsy.ddl.entity.WordType;
import link.dwsy.ddl.repository.ContentChecking.WordReptitory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class MyDdWordAllow implements IWordAllow {

    @Resource
    private WordReptitory wordReptitory;

    @Override
    public List<String> allow() {

        List<WordEntity> wordList = wordReptitory.findByDeletedFalseAndType(WordType.Allow);

        return wordList.stream().map(WordEntity::getWord).collect(Collectors.toList());
    }

}