package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Message.NotifyState;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.VO.UnreadNotifyVo;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserNotify;
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

        for (UserNotify notify : replyMeNotify) {
            User formUser = userRepository.findUserByIdAndDeletedIsFalse(notify.getFromUserId());
            notify.setFormUserAvatar(formUser.getUserInfo().getAvatar());
            notify.setFormUserNickname(formUser.getNickname());
            if (notify.getNotifyState() == NotifyState.UNREAD) {
                notify.setNotifyState(NotifyState.READ);
                userNotifyRepository.save(notify);
                notify.setNotifyState(NotifyState.UNREAD);
            }
        }
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
        for (UserNotify notify : ThumbUpMeNotify) {
            User formUser = userRepository.findUserByIdAndDeletedIsFalse(notify.getFromUserId());
            notify.setFormUserAvatar(formUser.getUserInfo().getAvatar());
            notify.setFormUserNickname(formUser.getNickname());
            if (notify.getNotifyState() == NotifyState.UNREAD) {
                notify.setNotifyState(NotifyState.READ);
                userNotifyRepository.save(notify);
                notify.setNotifyState(NotifyState.UNREAD);
            }
        }
        return new PageData<>(ThumbUpMeNotify);
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
            unreadNotifyVo.setUnreadNotifyCount(NotifyCount);
            return unreadNotifyVo;
        }
        if (type.equals("detail")) {
            int ReplyCommentCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
                            (userId, NotifyState.UNREAD, List.of(NotifyType.comment_article, NotifyType.comment_article_comment));

            int ThumbUpCount = userNotifyRepository
                    .countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
                            (userId, NotifyState.UNREAD, List.of(NotifyType.up_article, NotifyType.up_article_comment));

            unreadNotifyVo.setUnreadNotifyThumbCount(ThumbUpCount);
            unreadNotifyVo.setUnreadNotifyReplyCommentCount(ReplyCommentCount);
            return unreadNotifyVo;
        }
        return unreadNotifyVo;
    }
}
