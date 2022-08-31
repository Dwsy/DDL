package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;

public interface QaQuestionFieldService {
    PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest);

    QaQuestionField getQuestionById(long qid);
}
