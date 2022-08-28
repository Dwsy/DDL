package link.dwsy.ddl.repository;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.ArticleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleGroupRepository extends JpaRepository<ArticleGroup,Long> {
    List<ArticleGroup> findAllByDeletedIsFalse();
}
