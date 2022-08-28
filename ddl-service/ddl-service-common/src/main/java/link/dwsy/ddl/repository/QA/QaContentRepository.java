package link.dwsy.ddl.repository.QA;



import link.dwsy.ddl.XO.VO.ContentHtmlVO;
import link.dwsy.ddl.XO.VO.ContentMdVO;
import link.dwsy.ddl.XO.VO.ContentPureVO;
import link.dwsy.ddl.entity.Article.ArticleContent;
import link.dwsy.ddl.entity.QA.QaQuestionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;


public interface QaContentRepository extends JpaRepository<QaQuestionContent, Long> {

    @Modifying
    @Transactional
    @Query(value = "update qa_question_content set qa_question_field_id=?1 where id=?2", nativeQuery = true)
    int setQuestionFieldLd(long fid, long cid);

}
