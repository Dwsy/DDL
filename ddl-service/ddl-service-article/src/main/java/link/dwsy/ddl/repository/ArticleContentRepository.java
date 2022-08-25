package link.dwsy.ddl.repository;

import link.dwsy.ddl.XO.DTO.ArticleList;
import link.dwsy.ddl.XO.DTO.ArticleListDTO;
import link.dwsy.ddl.entity.ArticleContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {
public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {

//    Page<ArticleContent> findAllByDeletedIsFalseOrDeletedIsNull(Pageable pageable);
//    Page<ArticleList> findAllByDeletedIsFalse(Pageable pageable);

//    ArticleContent findByTitleIsNotNull();
//    @EntityGraph(attributePaths = "articleTags")
    ArticleListDTO findByTitleIsNotNull();
//
}
