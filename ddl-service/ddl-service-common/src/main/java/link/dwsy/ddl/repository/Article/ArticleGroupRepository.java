package link.dwsy.ddl.repository.Article;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleGroupRepository extends JpaRepository<ArticleGroup,Long> {
    List<ArticleGroup> findAllByDeletedIsFalse();
}
