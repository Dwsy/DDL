package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    //    @Query(nativeQuery = true,"select count(id) from article_comment where article_content_id = :1")
    @Query("select count(articleContent) from ArticleComment  where articleContent.id = ?1 and deleted is false ")
    int getCountByArticleId(long id);
}
