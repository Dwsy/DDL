package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.RB.InvitationUserRB;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.XO.VO.InvitationUserVO;
import link.dwsy.ddl.XO.VO.UserAnswerVO;
import link.dwsy.ddl.controller.QuestionAnswerOrCommentActionRB;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;

public interface QaAnswerService {
    PageData<QaAnswer> getByQuestionId(long qid, PageRequest pageRequest);

    PageData<QaAnswer> getChildAnswerPageByParentId(Long qid, Long pid, PageRequest pageRequest);

    QaAnswer answer(QaAnswerRB qaAnswerRB, AnswerType answerType);

    boolean logicallyDelete(long questionId, long answerId);

    AnswerType action(QuestionAnswerOrCommentActionRB actionRB);

    void invitationUserAnswerQuestion(InvitationUserRB invitationUserRB);

    boolean acceptedAnswer(long answerId, boolean accepted);

    PageData<UserAnswerVO> getUserAnswerPageById(Long userId, PageRequest pageRequest);

    ArrayList<InvitationUserVO> getRecommendedUserByTagIds(TagIdsRB tagIdsRB, long questionId);

    @NotNull PageData<InvitationUserVO> getInvitationFollowerList(String order, String[] properties, int page, int size, int inviteUid, long questionId);

    @NotNull PageData<InvitationUserVO> getInvitationFollowingList(String order, String[] properties, int page, int size, long questionId);
}
