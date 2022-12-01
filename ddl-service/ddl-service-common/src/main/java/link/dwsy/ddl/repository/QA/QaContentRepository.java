package link.dwsy.ddl.repository.QA;


import link.dwsy.ddl.entity.QA.QaQuestionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;


public interface QaContentRepository extends JpaRepository<QaQuestionContent, Long> {

    @Modifying
    @Transactional
    @Query(value = "update qa_question_content set qa_question_field_id=?1 where id=?2", nativeQuery = true)
    int setQuestionFieldLd(long fid, long cid);

    @Modifying
    @Transactional
    @Query(value = "update qa_question_content set text_html=?1 where id=?2", nativeQuery = true)
    int setHtmlContent(long fid, String html);

    @Query(value = "select text_md from qa_question_content where qa_question_field_id=?1 and deleted is false", nativeQuery = true)
    String getMdTextById(long id);

    @Query(value = "select text_pure from qa_question_content where qa_question_field_id=?1 and deleted is false", nativeQuery = true)
    String getPureTextById(long id);

    @Query(value = "select text_html from qa_question_content where qa_question_field_id=?1 and deleted is false", nativeQuery = true)
    String getHtmlTextById(long id);

    QaQuestionContent findByDeletedFalseAndQuestionFieldId(long questionFieldId);


    @Modifying
    @Transactional
    @Query(value = "update qa_question_content set deleted=?2 where qa_question_field_id=?1", nativeQuery = true)
    int updateDeleted(long questionFieldId, Boolean deleted);


}
