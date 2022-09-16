package link.dwsy.ddl.repository.Article;

//import link.dwsy.ddl.XO.DTO.OnlyTest;

import link.dwsy.ddl.entity.Article.ArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


//public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {
public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {
    @Query(value = "select text_md from article_content where article_field_id=?1 and deleted is false", nativeQuery = true)
    String getMdTextById(long id);

    @Query(value = "select text_pure from article_content where article_field_id=?1 and deleted is false", nativeQuery = true)
    String getPureTextById(long id);

    @Query(value = "select text_html from article_content where article_field_id=?1 and deleted is false", nativeQuery = true)
    String getHtmlTextById(long id);

    @Modifying
    @Transactional
    @Query(value = "update article_content set article_field_id=?1 where id =?2", nativeQuery = true)
    int setArticleFieldId(long fid, long cid);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_content set deleted=true where id=?1")
    int logicallyDeleted(long aid);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_content set deleted=false where id=?1")
    int logicallyRecovery(long aid);

//    contentMdVO findByIdAndDeletedIsFalse(long id);

//    Page<ArticleContent> findAllByDeletedIsFalseOrDeletedIsNull(Pageable pageable);
//    Page<ArticleList> findAllByDeletedIsFalse(Pageable pageable);

    //    ArticleContent findByTitleIsNotNull();
//    @EntityGraph(attributePaths = "articleTags")
//    ArticleListDTO findByTitleIsNotNull();

    //
//    OnlyTest findByVbyId(Long id);
//    @Query(nativeQuery = true, value= "select articlecon0_.id,\n" +
//            "       articlecon0_.title,\n" +
//            "       articletag4_.id as article_tag_id,\n" +
//            "       articletag4_.create_time,\n" +
//            "       articletag4_.deleted,\n" +
//            "       articletag4_.last_modified_time,\n" +
//            "       articletag4_.article_num,\n" +
//            "       articletag4_.name,\n" +
//            "       articletag4_.tag_info\n" +
//            "from article_content articlecon0_\n" +
//            "         inner join\n" +
//            "     article_tag_ref articletag3_ on articlecon0_.id = articletag3_.article_content_id\n" +
//            "         inner join\n" +
//            "     article_tag articletag4_ on articletag3_.article_tag_id = articletag4_.id\n" +
//            "where articlecon0_.id = 73;")
//    @Query("select a.id,a.title,a.articleTags  from ArticleContent as a left join a.articleTags where a.id =?1")
//    Set<OnlyTest> findAllById(Long id);

//
}
