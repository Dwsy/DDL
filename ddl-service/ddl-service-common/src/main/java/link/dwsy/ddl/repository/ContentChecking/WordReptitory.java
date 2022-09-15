package link.dwsy.ddl.repository.ContentChecking;

import link.dwsy.ddl.entity.ContentChecking.WordEntity;
import link.dwsy.ddl.entity.WordType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface WordReptitory extends JpaRepository<WordEntity, Long> {


    List<WordEntity> findByDeletedFalseAndType(WordType type);

    WordEntity findByDeletedFalseAndWord(String word);
}
