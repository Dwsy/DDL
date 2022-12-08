package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.RB.InvitationUserRB;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.XO.VO.InvitationUserVO;
import link.dwsy.ddl.XO.VO.UserAnswerVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.service.RPC.UserService;
import link.dwsy.ddl.service.impl.QaAnswerServiceServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */
@RestController
@RequestMapping("answer")
@Slf4j
public class QaAnswerController {
    @Resource
    private UserService userService;
    @Resource
    private QaAnswerServiceServiceImpl qaAnswerServiceService;
    @Resource
    private UserStateService userStateService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private UserNotifyRepository userNotifyRepository;

    @GetMapping("/{id}")
    public PageData<QaAnswer> getAnswerPageById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("id") Long qid) {
        if (qid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return qaAnswerServiceService.getByQuestionId(qid, pageRequest);
    }

    @GetMapping("/child/{qid}-{pid}")
    public PageData<QaAnswer> getChildAnswerPageById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
//            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
//            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("qid") Long qid,
            @PathVariable("pid") Long pid
    ) {
        if (qid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", page, size);
        return qaAnswerServiceService.getChildAnswerPageByParentId(qid, pid, pageRequest);
    }

    @PostMapping()
    public QaAnswer answer(@Validated @RequestBody QaAnswerRB qaAnswerRB) {
        return qaAnswerServiceService.answer(qaAnswerRB, qaAnswerRB.getAnswerType());
    }

    @AuthAnnotation(Level = 0)
    @DeleteMapping("/{questionId}/{answerId}")//todo time
//    logically delete a comment
    public boolean delete(@PathVariable long questionId, @PathVariable long answerId) {
        return qaAnswerServiceService.logicallyDelete(questionId, answerId);
    }

    @PostMapping("/action")
    @AuthAnnotation
    public int action(@Validated @RequestBody QuestionAnswerOrCommentActionRB actionRB) {
        AnswerType action = qaAnswerServiceService.action(actionRB);
        return action.ordinal();
    }

    @GetMapping("/accept")
    @AuthAnnotation
    public boolean acceptedAnswer(@RequestParam long answerId,
                                  @RequestParam boolean accepted) {
        return qaAnswerServiceService.acceptedAnswer(answerId, accepted);
    }

    @PostMapping("invitation")
    @AuthAnnotation
    public void invitationUserAnswerQuestion(@Validated @RequestBody InvitationUserRB invitationUserRB) {
        qaAnswerServiceService.invitationUserAnswerQuestion(invitationUserRB);
    }

    @GetMapping("invitation/following/{questionId}")
    @AuthAnnotation
    public PageData<InvitationUserVO> getInvitationFollowingList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "20", name = "size") int size,
            @PathVariable long questionId) {
        return qaAnswerServiceService.getInvitationFollowingList(order, properties, page, size, questionId);
    }


    @GetMapping("invitation/follower/{questionId}")
    @AuthAnnotation
    public PageData<InvitationUserVO> getInvitationFollowerList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "20", name = "size") int size,
            @RequestParam(required = false, defaultValue = "10", name = "inviteUid") int inviteUid,
            @PathVariable long questionId) {
        return qaAnswerServiceService.getInvitationFollowerList(order, properties, page, size, inviteUid, questionId);
    }


    @PostMapping("invitation/recommended/{questionId}")
    @AuthAnnotation
    public ArrayList<InvitationUserVO> getRecommendedUserByTagIds(
            @RequestBody TagIdsRB tagIdsRB,
            @PathVariable long questionId) {
        return qaAnswerServiceService.getRecommendedUserByTagIds(tagIdsRB, questionId);
    }


    ;

    @GetMapping("/user/list/{userId}")
    public PageData<UserAnswerVO> getUserAnswerPageById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("userId") Long userId) {
        if (userId < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        if (!userRepository.existsByDeletedFalseAndId(userId)) {
            throw new CodeException(CustomerErrorCode.UserCancellation);
        }
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return qaAnswerServiceService.getUserAnswerPageById(userId, pageRequest);
    }


}
