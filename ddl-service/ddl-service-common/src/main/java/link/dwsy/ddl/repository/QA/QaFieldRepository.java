package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QaFieldRepository extends JpaRepository<QaQuestionField, Long> {
    Page<QaQuestionField> findByDeletedFalseAndQuestionState(QuestionState questionState, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndQuestionStateIn
            (Collection<QuestionState> questionStates, Pageable pageable);

    QaQuestionField findByDeletedFalseAndId(long id);

    Optional<QaQuestionField> findByIdAndDeletedFalse(long id);

    Page<QaQuestionField> findByDeletedFalseAndIdIn(Collection<Long> id, Pageable pageable);

    Page<QaQuestionField> findByDeletedFalseAndIdInAndQuestionStateIn(Collection<Long> ids, Collection<QuestionState> questionStates, Pageable pageable);

    Optional<QaQuestionField> findByDeletedFalseAndIdAndQuestionState(long id, QuestionState questionState);



    List<QaQuestionField> findAllByDeletedIsFalse();

    Page<QaQuestionField> findByDeletedFalseAndQaGroupIdAndQuestionStateIn
            (long gid, Collection<QuestionState> questionStates, Pageable pageable);



//    findAllByDeletedIsFalseAndArticleGroupIdAndArticleState

//    Page<fieldVO> findAllByIdInAndDeletedIsFalseAndArticleState(long[] ids, ArticleState open, PageRequest of);



}
