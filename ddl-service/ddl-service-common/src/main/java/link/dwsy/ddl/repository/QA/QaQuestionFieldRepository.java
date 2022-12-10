package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QaQuestionFieldRepository extends JpaRepository<QaQuestionField, Long> {
    Page<QaQuestionField> findByDeletedFalseAndQuestionState(QuestionState questionState, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndQuestionStateNot(QuestionState questionState, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndUserIdAndQuestionState(long user_id, QuestionState questionState, Pageable pageable);
    Page<QaQuestionField> findByDeletedFalseAndUserIdAndQuestionStateIn(long user_id, Collection<QuestionState> questionStates, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndUserIdAndQuestionStateNot(long user_id, QuestionState questionState, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndQuestionStateIn
            (Collection<QuestionState> questionStates, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndQuestionStateInAndUser_DeletedFalse(Collection<QuestionState> questionStates, Pageable pageable);

    QaQuestionField findByDeletedFalseAndId(long id);

    QaQuestionField findByDeletedFalseAndIdAndQuestionStateNot(long id, QuestionState questionState);


    Optional<QaQuestionField> findByIdAndDeletedFalse(long id);

    Page<QaQuestionField> findByDeletedFalseAndIdIn(Collection<Long> id, Pageable pageable);

    List<QaQuestionField> findAllByDeletedFalseAndIdIn(Collection<Long> id);

    List<QaQuestionField> findAllByDeletedFalseAndIdIn(Collection<Long> id, Sort sort);

    Page<QaQuestionField> findByDeletedFalseAndIdInAndQuestionStateIn(Collection<Long> ids, Collection<QuestionState> questionStates, Pageable pageable);

    QaQuestionField findByDeletedFalseAndIdAndQuestionStateIn(long id, Collection<QuestionState> questionStates);


    Optional<QaQuestionField> findByDeletedFalseAndIdAndQuestionState(long id, QuestionState questionState);


    List<QaQuestionField> findAllByDeletedIsFalse();

    Page<QaQuestionField> findByDeletedFalseAndQaGroupIdAndQuestionStateIn
            (long gid, Collection<QuestionState> questionStates, Pageable pageable);

    @Query(nativeQuery = true,
            value = "update qa_question_field set collect_num = collect_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void collectNumIncrement(long sid, int i);

    @Query(nativeQuery = true,
            value = "update qa_question_field set answer_num = answer_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void answerNumIncrement(long qid, int i);

    @Query(nativeQuery = true,
            value = "update qa_question_field set up_num = up_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void upNumIncrement(long sid, int i);

    @Query(nativeQuery = true,
            value = "update qa_question_field set down_num = down_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void downNumIncrement(long sid, int i);

    @Query(nativeQuery = true,
            value = "update qa_question_field set view_num = view_num+?2 where id = ?1")
    @Modifying
    @Transactional
    void viewNumIncrement(long sid, int i);

    boolean existsByDeletedFalseAndAllowAnswerTrueAndId(long id);

    @Query(nativeQuery = true,
            value = "select * from qa_question_field where deleted is false and question_state!=4")
        //question_state 4 = hide
    long[] findAllId();

    @Query(nativeQuery = true, value =
            "select user_id from qa_question_field where id=?1 and deleted is false")
    Long getUserIdByQuestionId(Long questionId);

    @Query(nativeQuery = true, value =
            "select user_id from qa_question_field where id=?1 and deleted is false")
    Long getUserIdByQuestionFieldId(Long questionId);


    @Query(nativeQuery = true,
            value = "select title from qa_question_field  where deleted = false and id = ?1")
    String getTitle(Long questionId);

    @Query(nativeQuery = true,
            value = "select title from qa_question_field  where deleted = false and id = (select question_field_id from qa_answer where id = ?1)")
    String getTitleByAnswerId(Long answerId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into qa_user_watch_ref (user_id, question_id,delete,create_time) values (?1, ?2,false,now())")
    int watchQuestion(Long userId, long questionId);


    @Query(nativeQuery = true,
            value = "select count(*) from qa_user_watch_ref where user_id = ?1 and question_id = ?2 and delete = false")
    int isWatch(long userId, long questionId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from qa_user_watch_ref where user_id = ?1 and question_id = ?2")
    int cancelWatchQuestion(Long userId, long questionId);

    @Query(nativeQuery = true,
            value = "select user_id from qa_user_watch_ref where question_id = ?1")
    long[] getWatchUser(long questionId);

    int countByDeletedFalseAndUser_IdAndQuestionState(long id, QuestionState questionState);

    int countByDeletedFalseAndUser_IdAndQuestionStateNot(long id, QuestionState questionState);
//    findAllByDeletedIsFalseAndArticleGroupIdAndArticleState

//    Page<fieldVO> findAllByIdInAndDeletedIsFalseAndArticleState(long[] ids, ArticleState open, PageRequest of);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update qa_question_field set question_state = ?2 where id = ?1")
    int setQuestionState(long id, QuestionState questionState);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update qa_question_field set question_state = ?2 where id = ?1 and question_state = ?3")
    void setQuestionStateIfNowStateIs(long id, int questionState, int nowState);


    @Modifying
    @Transactional
    @Query(value = "update qa_question_field set deleted=?2 where id=?1", nativeQuery = true)
    int updateDeleted(long questionFieldId, Boolean deleted);

    @Query(nativeQuery = true, value = "select count(*) from users where deleted is true and " +
            "id=(select user_id from qa_question_field  where deleted is false and id=?1) and  deleted = true")
    int userIsCancellation(long id);

    @Query(nativeQuery = true,value = "select question_field_id from qa_answer where id=?1")
    long getIdByAnswerId(long answerId);

    @Query(nativeQuery = true,value = "select title from qa_question_field where id=" +
            "(select question_field_id from qa_answer where id=?1)")
    String getTitleByAnswerId(long answerId);

    @Query(nativeQuery = true,value = "select count(*) from users where id=(select user_id  from qa_question_field where id=?1) and deleted is true")
    int userIsCancelled(long answerId);

    @Query(nativeQuery = true, value = "select qa_group_id from qa_question_field where id=?1")
    long getGroupIdByQuestionId(long id);

    @Query(nativeQuery = true, value = "select qa_field_id from qa_tag_ref where qa_field_id=?1")
    List<Long> getTagIdListByQuestionId(long id);
}
