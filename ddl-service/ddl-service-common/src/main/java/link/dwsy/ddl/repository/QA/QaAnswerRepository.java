package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.entity.QA.QaAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QaAnswerRepository extends JpaRepository<QaAnswer, Long> {

    Optional<QaAnswer> findByDeletedFalseAndId(long id);

    List<QaAnswer> findByUser_LevelBetween(int levelStart, int levelEnd, Pageable pageable);

    //    @Query(value = "select a.questionField.id from QaAnswer a where a.id=?2 and a.questionField.id=?1")
    boolean existsByIdAndQuestionFieldId(long qid, long aid);

    @Query(value = "select parent_answer_id=0 from qa_answer a where id=?1", nativeQuery = true)
    boolean isFirstAnswer(long aid);

    @Query(value = "select parent_user_id from qa_answer where id=?1", nativeQuery = true)
    long findParentUserIdByAnswerId(long aid);

    @Query(value = "select user_id from qa_answer where id=?1", nativeQuery = true)
    long findUserIdByAnswerId(long aid);

    Page<QaAnswer> findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(long aid, long pid, Pageable pageable);

    Page<QaAnswer> findByDeletedFalseAndQuestionField_IdAndAnswerTypeAndParentAnswerId(long id, AnswerType answerType, long parentAnswerId, Pageable pageable);


    List<QaAnswer> findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(long aid, long pid);

    Page<QaAnswer> findByDeletedFalseAndQuestionField_IdAndAnswerType(long id, AnswerType answerType, Pageable pageable);


    Optional<QaAnswer> findByDeletedFalseAndUser_IdAndParentAnswerIdAndAnswerTypeIn
            (long id, long parentAnswerId, Collection<AnswerType> answerTypes);

    QaAnswer findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
            (long id, long parentAnswerId, AnswerType answerType);

    boolean existsByDeletedFalseAndIdAndQuestionField_IdAndAnswerType(long id, long qid, AnswerType answerType);

    boolean existsByDeletedFalseAndIdAndQuestionField_IdAndAnswerTypeIn(long id, long qid, Collection<AnswerType> answerTypes);

    @Query(nativeQuery = true, value =
            "select user_id from qa_answer where id=?1 and deleted=false")
    Long getUserIdByAnswerId(Long id);

    @Query(nativeQuery = true, value = "select text_pure from qa_answer where id=?1 and deleted=false")
    String getPureText(Long answerId);

    @Query(nativeQuery = true, value = "select text_html from qa_answer where id=?1 and deleted=false")
    String getHtmlText(Long answerId);
//    findByUserIdAndParentCommentIdAndCommentTypeIn


    QaAnswer findByDeletedFalseAndUser_IdAndQuestionField_IdAndParentAnswerIdAndAnswerTypeIn
            (long id, long id1, long parentAnswerId, Collection<AnswerType> answerTypes);

    boolean existsByDeletedFalseAndUser_IdAndQuestionField_IdAndParentAnswerIdAndAnswerTypeIn
            (long id, long id1, long parentAnswerId, Collection<AnswerType> answerTypes);

    @Query(nativeQuery = true,
            value = "update qa_answer set up_num = up_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void upNumIncrement(long actionAnswerOrCommentId, int i);

    @Transactional
    @Modifying
    @Query("update QaAnswer q set q.answerType = ?1 where q.deleted = false and q.id = ?2")
    void updateCommentTypeByIdAndDeletedFalse(AnswerType answerType, long id);

    @Query(nativeQuery = true,
            value = "update qa_answer set down_num = down_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void downNumIncrement(long actionAnswerOrCommentId, int i);

    QaAnswer findByDeletedFalseAndIdAndAnswerType(long id, AnswerType answerType);

    @Query(nativeQuery = true,
            value = "select question_field_id from qa_answer where id=?1")
    long findQuestionIdByAnswerId(long answerId);


}
