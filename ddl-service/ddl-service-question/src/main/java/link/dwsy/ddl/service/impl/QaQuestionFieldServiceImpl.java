package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.QA.QaFieldRepository;
import link.dwsy.ddl.service.QaQuestionFieldService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class QaQuestionFieldServiceImpl implements QaQuestionFieldService {

    @Resource
    QaFieldRepository qaFieldRepository;

    public PageData<QaQuestionField> getPageList(int page, int size, Collection<QuestionState> questionStateCollection, Sort sort) {
        PageRequest pageRequest = PageRequest.of(page - 1, size,sort);

        Page<QaQuestionField> questionFields = qaFieldRepository
                .findByDeletedFalseAndQuestionStateIn(questionStateCollection, pageRequest);

        PageData<QaQuestionField> fieldPageData = new PageData<>(questionFields);
        return fieldPageData;
    }

    public QaQuestionField getQuestionById(long qid) {
        QaQuestionField questionField = qaFieldRepository.findByDeletedFalseAndId(qid);
        return questionField;
    }

}
