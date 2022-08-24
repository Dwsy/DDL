package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    
}
