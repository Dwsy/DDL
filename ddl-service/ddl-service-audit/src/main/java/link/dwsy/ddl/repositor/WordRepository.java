package link.dwsy.ddl.repositor;


import link.dwsy.ddl.XO.Enmu.WordType;
import link.dwsy.ddl.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface WordRepository extends JpaRepository<WordEntity, Long> {


    List<WordEntity> findByDeletedFalseAndType(WordType type);

    WordEntity findByDeletedFalseAndWord(String word);
}
