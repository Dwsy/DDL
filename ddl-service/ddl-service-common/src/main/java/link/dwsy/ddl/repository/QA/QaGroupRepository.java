package link.dwsy.ddl.repository.QA;

import link.dwsy.ddl.entity.QA.QaGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaGroupRepository extends JpaRepository<QaGroup,Long> {

    List<QaGroup> findAllByDeletedIsFalse();

}
