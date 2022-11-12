package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.XO.Enum.Message.NotifyState;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.VO.UnreadNotifyVo;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/9/30
 */
@RestController
@RequestMapping("/notify")
public class UserNotifyController {

    @Resource
    UserMessageRepository userMessageRepository;
    @Resource
    private UserNotifyRepository userNotifyRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private UserRepository userRepository;

    @GetMapping("reply")
    @AuthAnnotation
    public PageData<UserNotify> getReplyMe(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, 15);
        Page<UserNotify> replyMeNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyTypeIn
                        (userId, Set.of(NotifyType.comment_article, NotifyType.comment_article_comment), pageRequest);

        read(replyMeNotify);
        return new PageData<>(replyMeNotify);
    }

    @GetMapping("thumb")
    @AuthAnnotation
    public PageData<UserNotify> getThumbUpMe(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        Page<UserNotify> ThumbUpMeNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyTypeIn
                        (userId, Set.of(NotifyType.up_article, NotifyType.up_article_comment), pageRequest);
        read(ThumbUpMeNotify);
        return new PageData<>(ThumbUpMeNotify);
    }

    @GetMapping("qa/comment")
    @AuthAnnotation
    public PageData<UserNotify> getQaComment(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type//0 all 1 question 2 answer
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        ArrayList<NotifyType> notifyTypes = new ArrayList<>();
        if (type == 0) {
            notifyTypes.add(NotifyType.question_comment);
            notifyTypes.add(NotifyType.answer_comment);
        } else if (type == 1) {
            notifyTypes.add(NotifyType.question_comment);
        } else if (type == 2) {
            notifyTypes.add(NotifyType.answer_comment);
        }
        Page<UserNotify> qaCommentNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyTypeIn(userId, notifyTypes, pageRequest);
        read(qaCommentNotify);
        return new PageData<>(qaCommentNotify);
    }

    @GetMapping("qa/support")
    @AuthAnnotation
    public PageData<UserNotify> getQaSupport(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        ArrayList<NotifyType> notifyTypes = new ArrayList<>();
        if (type == 0) {
            notifyTypes.add(NotifyType.up_question_answer);
            notifyTypes.add(NotifyType.up_question);
        } else if (type == 1) {
            notifyTypes.add(NotifyType.up_question_answer);
        } else if (type == 2) {
            notifyTypes.add(NotifyType.up_question);
        }
        Page<UserNotify> QaSupportNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyTypeIn(userId, notifyTypes, pageRequest);
        read(QaSupportNotify);
        return new PageData<>(QaSupportNotify);
    }

    @GetMapping("qa/answer")
    @AuthAnnotation
    public PageData<UserNotify> getQaAnswer(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        Page<UserNotify> AnswerNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyType
                        (userId, NotifyType.answer, pageRequest);
        read(AnswerNotify);
        return new PageData<>(AnswerNotify);
    }

    @GetMapping("qa/invitationAnswer")
    @AuthAnnotation
    public PageData<UserNotify> getInvitationAnswer(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        Page<UserNotify> AnswerNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyType
                        (userId, NotifyType.invitation_user_answer_question, pageRequest);
        read(AnswerNotify);

        return new PageData<>(AnswerNotify);
    }


    @GetMapping("qa/accepted")
    @AuthAnnotation
    public PageData<UserNotify> getAcceptedAnswer(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "15", name = "size") int size
    ) {
        Long userId = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(Sort.Direction.DESC, "createTime", page, size);
        Page<UserNotify> AnswerNotify = userNotifyRepository
                .findByDeletedFalseAndToUserIdAndNotifyType
                        (userId, NotifyType.accepted_question_answer, pageRequest);
        read(AnswerNotify);

        return new PageData<>(AnswerNotify);
    }
    //    invitationAnswer
    private void read(Page<UserNotify> QaSupportNotify) {
        for (UserNotify notify : QaSupportNotify) {
            User formUser = userRepository.findUserByIdAndDeletedIsFalse(notify.getFromUserId());
            notify.setFormUserAvatar(formUser.getUserInfo().getAvatar());
            notify.setFormUserNickname(formUser.getNickname());
            if (notify.getNotifyState() == NotifyState.UNREAD) {
                notify.setNotifyState(NotifyState.READ);
                userNotifyRepository.save(notify);
                notify.setNotifyState(NotifyState.UNREAD);
            }
        }
    }

    //    export enum CountType {
//        all,//只显示总数
//        detail//明细
//    }
    @GetMapping("unread")
    @AuthAnnotation
    public UnreadNotifyVo getUserUnreadNotifyNum(
            @RequestParam(required = false, defaultValue = "all", name = "type") String type
    ) {
        UnreadNotifyVo unreadNotifyVo = new UnreadNotifyVo();
        Long userId = userSupport.getCurrentUser().getId();

        if (type.equals("all")) {
            int NotifyCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyState(userId, NotifyState.UNREAD);
            int userUnreadPrivateMsgCount = userMessageRepository.countByToUserIdAndStatus(userId, MessageState.UNREAD);
            unreadNotifyVo.setUnreadNotifyCount(NotifyCount + userUnreadPrivateMsgCount);

            return unreadNotifyVo;
        }
        if (type.equals("detail")) {
            int ReplyCommentCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
                            (userId, NotifyState.UNREAD, List.of(NotifyType.comment_article, NotifyType.comment_article_comment));

            int ArticleOrCommentThumbCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
                            (userId, NotifyState.UNREAD,
                                    List.of(NotifyType.up_article, NotifyType.up_article_comment, NotifyType.up_question, NotifyType.up_question_answer));

            int QuestionOrAnswerThumbCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
                            (userId, NotifyState.UNREAD,
                                    List.of(NotifyType.up_question, NotifyType.up_question_answer));

            int AnswerCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
                            (userId, NotifyState.UNREAD, NotifyType.answer);

            int AnswerCommentCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
                            (userId, NotifyState.UNREAD, NotifyType.answer_comment);

            int QuestionCommentCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
                            (userId, NotifyState.UNREAD, NotifyType.question_comment);

            int userUnreadPrivateMsgCount = userMessageRepository.countByToUserIdAndStatus(userId, MessageState.UNREAD);

            int userUnreadInvitationAnswerCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
                            (userId, NotifyState.UNREAD, NotifyType.invitation_user_answer_question);

            int unreadAdoptAnswerCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
                            (userId, NotifyState.UNREAD, NotifyType.accepted_question_answer);

            unreadNotifyVo.setUnreadNotifyArticleOrCommentThumbCount(ArticleOrCommentThumbCount);
            unreadNotifyVo.setUnreadNotifyQuestionOrAnswerThumbCount(QuestionOrAnswerThumbCount);
            unreadNotifyVo.setUnreadNotifyReplyCommentCount(ReplyCommentCount);
            unreadNotifyVo.setUnreadNotifyAnswerCount(AnswerCount);
            unreadNotifyVo.setUnreadNotifyAnswerCommentCount(AnswerCommentCount);
            unreadNotifyVo.setUnreadNotifyQuestionCommentCount(QuestionCommentCount);
            unreadNotifyVo.setUnreadPrivateMessageCount(userUnreadPrivateMsgCount);
            unreadNotifyVo.setUnreadInvitationAnswerCount(userUnreadInvitationAnswerCount);
            unreadNotifyVo.setUnreadAcceptedAnswerCount(unreadAdoptAnswerCount);
            return unreadNotifyVo;
        }
        return unreadNotifyVo;
    }
}
