package link.dwsy.ddl.XO.CustomER.repository;

import link.dwsy.ddl.XO.CustomER.entity.ArticleCommentCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ArticleCommentRepositoryCustom extends JpaRepository<ArticleCommentCustom, Long> {
    //    @Query("select ArticleCommentCustom from ArticleCommentCustom as acc where acc.articleContent.id=?1 and deleted is false ")
    Page<ArticleCommentCustom> findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(Long aid,Long pid ,Pageable pageable);

    Set<ArticleCommentCustom> findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(Long aid, Long pid);
}
