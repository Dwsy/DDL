package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.service.impl.UserPrivateMessageServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */
@RestController
@RequestMapping("private/message")
public class UserPrivateMessageController {


    @Resource
    private UserPrivateMessageServiceImpl userPrivateMessageService;

    @Resource
    private UserMessageRepository userMessageRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserSupport userSupport;

    @Resource
    private UserStateService userStateService;

    @GetMapping("/list")
    @AuthAnnotation
    public PageData<UserMessage> getPrivateMessageList(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "desc", name = "order") String order,
            @RequestParam(required = false, defaultValue = "conversation_id", name = "properties") String[] properties
    ) {
        Long uid = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        Page<UserMessage> privateMessagePage = userMessageRepository.getPrivateMessageListPage(uid, pageRequest);
        for (UserMessage message : privateMessagePage) {
            long chatUserId;
            if (message.getToUserId() == uid) {
                chatUserId = message.getFormUserId();
            } else {
                chatUserId = message.getToUserId();
            }
            User chatUser = userRepository.findUserById(chatUserId);
            userStateService.cancellationUserHandel(chatUser);
          int unreadMsgCount= userPrivateMessageService.getUnreadMsgCount(uid, chatUserId);
            message.setChatUserId(chatUserId);
            message.setChatUserNickname(chatUser.getNickname());
            message.setUnreadMsgCount(unreadMsgCount);
            message.setChatUserAvatar(chatUser.getUserInfo().getAvatar());
        }
        return new PageData<>(privateMessagePage);
    }

    @PutMapping("/send")
    @AuthAnnotation
    public boolean sendMessage(@Validated @RequestBody
                               SendPrivateMessageRB sendPrivateMessageRB) throws Exception {
        if (sendPrivateMessageRB.getToUserId() <= 0) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        return userPrivateMessageService.sendPrivateMessage(sendPrivateMessageRB);
    }

    @GetMapping("/pull/{toUserId}")
    @AuthAnnotation
    public PageData<UserMessage> pullMessageByLatestId(
            @RequestParam(required = false, name = "size", defaultValue = "10") int size,
            @RequestParam(required = false, name = "page", defaultValue = "1") int page,
            @RequestParam(required = false, name = "latest", defaultValue = "0") int latestId,
            @PathVariable long toUserId,
            @RequestParam(required = false, defaultValue = "desc", name = "order") String order,
            @RequestParam(required = false, defaultValue = "id", name = "properties") String[] properties) {

        PageRequest pageRequest = PRHelper.order(order, properties, page, size);

        return userPrivateMessageService.pullMessageByLatestId(latestId, toUserId, pageRequest);
    }

    @GetMapping("pull/history/{toUserId}")
    @AuthAnnotation
    public PageData<UserMessage> pullHistoryMessage(
            @RequestParam(required = false, name = "size", defaultValue = "10") int size,
            @RequestParam(required = false, name = "page", defaultValue = "1") int page,
            @RequestParam(required = false, name = "latest", defaultValue = "0") int latestId,
            @PathVariable long toUserId,
            @RequestParam(required = false, defaultValue = "desc", name = "order") String order,
            @RequestParam(required = false, defaultValue = "id", name = "properties") String[] properties) {
//        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        PageData<UserMessage> userMessagePageData = userPrivateMessageService
                .pullHistoryMessage(latestId, toUserId, page, size);

        return userMessagePageData;
    }

    @PostMapping("/read/{toUserId}")
    @AuthAnnotation
    public boolean readMessage(@PathVariable long toUserId) {
        return userPrivateMessageService.readMessage(toUserId);
    }
}
