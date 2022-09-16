package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class QaQuestionFieldServiceImpl implements link.dwsy.ddl.service.QaQuestionFieldService {

    @Resource
    QaQuestionFieldRepository qaQuestionFieldRepository;

    @Override
    public PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest) {

        Page<QaQuestionField> questionFields = qaQuestionFieldRepository
                .findByDeletedFalseAndQuestionStateIn(questionStateCollection, pageRequest);
        PageData<QaQuestionField> fieldPageData = new PageData<>(questionFields);
        return fieldPageData;
    }

    @Override
    public QaQuestionField getQuestionById(long qid) {
        QaQuestionField questionField = qaQuestionFieldRepository.findByDeletedFalseAndId(qid);
        return questionField;
    }

}
