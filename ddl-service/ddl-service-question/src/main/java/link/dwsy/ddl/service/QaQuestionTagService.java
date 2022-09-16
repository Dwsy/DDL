package link.dwsy.ddl.service;

import link.dwsy.ddl.entity.QA.QaTag;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface QaQuestionTagService {

    List<QaTag> getTagListByGroupId(Long id, Sort sort);
}
