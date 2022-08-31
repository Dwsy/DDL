package link.dwsy.ddl.service;

import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

public interface QaAnswerService {
    public PageData<QaAnswer> getByQuestionId(long qid, PageRequest pageRequest);
}
