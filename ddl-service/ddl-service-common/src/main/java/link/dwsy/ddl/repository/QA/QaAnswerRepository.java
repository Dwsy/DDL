package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.QA.QaAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface QaAnswerRepository extends JpaRepository<QaAnswer, Long> {

    //    @Query(value = "select a.questionField.id from QaAnswer a where a.id=?2 and a.questionField.id=?1")
    boolean existsByIdAndQuestionFieldId(long qid, long aid);

    @Query(value = "select parent_answer_id=0 from qa_answer a where id=?1", nativeQuery = true)
    boolean isFirstAnswer(long aid);

    @Query(value = "select parent_user_id from qa_answer where id=?1",nativeQuery = true)
    long findParentUserIdByAnswerId(long aid);

    @Query(value = "select user_id from qa_answer where id=?1",nativeQuery = true)
    long findUserIdByAnswerId(long aid);


}