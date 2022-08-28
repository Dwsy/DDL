package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    //    @Query(nativeQuery = true,"select count(id) from article_comment where article_content_id = :1")
    @Query("select count(articleField) from ArticleComment where articleField.id = ?1 and deleted is false ")
    int getCountByArticleId(long id);

    Page<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid, Pageable pageable);

    Set<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid);



}
