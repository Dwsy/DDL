package link.dwsy.ddl.repository.Article;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleGroupRepository extends JpaRepository<ArticleGroup,Long> {
    List<ArticleGroup> findAllByDeletedIsFalse(Sort sort);

    boolean existsByName(String name);


    @Query(value = "update article_group set deleted=true ,last_modified_time=now() where id=?1",nativeQuery = true)
    @Modifying
    @Transactional
    int logicallyDeleteById(Long id);
}
