package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.entity.QA.QaTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface QaQuestionTagRepository extends JpaRepository<QaTag, Long> {

    List<QaTag> findAllByDeletedIsFalse();

    @Query(value = "select qa_field_id from qa_tag_ref as ref where qa_tag_id=?1", nativeQuery = true)
    Collection<Long> findQuestionContentIdListById(Long id);
}
