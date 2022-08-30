package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.repository.QA.QaFieldRepository;
import link.dwsy.ddl.repository.QA.QaQuestionTagRepository;
import link.dwsy.ddl.service.QaQuestionTagService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class QaQuestionTagServiceImpl implements QaQuestionTagService {

    @Resource
    QaQuestionTagRepository qaQuestionTagRepository;

    @Resource
    QaFieldRepository qaFieldRepository;
    public List<QaTag> getTagList(Sort sort) {

        return qaQuestionTagRepository.findByDeletedFalse(sort);

    }

    public PageData<QaQuestionField> getQuestionListById(Long id, Set<QuestionState> questionStates, PageRequest pageRequest ) {
        Collection<Long> ids = qaQuestionTagRepository.findQuestionContentIdListById(id);

        Page<QaQuestionField> questionFields = qaFieldRepository
                .findByDeletedFalseAndIdInAndQuestionStateIn(ids, questionStates,pageRequest);
        return new PageData<>(questionFields);
    }
}
