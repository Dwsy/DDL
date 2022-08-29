package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaGroup;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.QA.QaFieldRepository;
import link.dwsy.ddl.repository.QA.QaGroupRepository;
import link.dwsy.ddl.service.QaQuestionGroupService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class QaQuestionGroupServiceImpl implements QaQuestionGroupService {


    @Resource
    QaGroupRepository qaGroupRepository;

    @Resource
    QaFieldRepository qaFieldRepository;

    public PageData<QaQuestionField> getFieldListByGroupId(Long gid, Set<QuestionState> questionStates,PageRequest pageRequest) {
        Page<QaQuestionField> questionFieldPage = qaFieldRepository.
                findByDeletedFalseAndQaGroupIdAndQuestionStateIn
                        (gid, questionStates, pageRequest);

        return new PageData<>(questionFieldPage);
    }

    public List<QaGroup> getGroupList(Sort sort) {
        return qaGroupRepository.findAllByDeletedIsFalse(sort);
    }
}
