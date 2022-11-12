package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaGroup;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface QaQuestionGroupService {

    PageData<QaQuestionField> getFieldListByGroupId(Long gid, Set<QuestionState> questionStates, PageRequest pageRequest);

    List<QaGroup> getGroupList(Sort sort);
}
