package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.RB.CreateQuestionRB;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;

public interface QaQuestionFieldService {
    PageData<QaQuestionField> getPageList(Collection<QuestionState> questionStateCollection, PageRequest pageRequest);

    PageData<QaQuestionField> getPageListManage(long userId, QuestionState questionState, PageRequest pageRequest);

    QaQuestionField getQuestionById(long qid, boolean getQuestionComment);

    QaQuestionField getQuestionByIdAndVersion(Long id, int version);

    long createQuestion(CreateQuestionRB createQuestionRB);

    long updateQuestion(CreateQuestionRB createQuestionRB);

    void view(Long id);

    UserActionVO getUserToQuestionAction(long questionId);

    boolean watchQuestion(long questionId);

    boolean unWatchQuestion(long questionId);

    PageData<QaQuestionField> getPageListByUserId(Long userId, QuestionState state, PageRequest pageRequest);
}
