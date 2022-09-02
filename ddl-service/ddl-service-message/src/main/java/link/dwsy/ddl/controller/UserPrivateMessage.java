package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import link.dwsy.ddl.service.impl.UserPrivateMessageServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */
@RestController
@RequestMapping("private/message")
public class UserPrivateMessage {


    @Resource
    private UserPrivateMessageServiceImpl userPrivateMessageService;

    @Resource
    private UserMessageRepository userMessageRepository;

    @Resource
    private UserSupport userSupport;

    @GetMapping("/list")
    public List<UserMessage> getPrivateMessageList(
//            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
//            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
//            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
//            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties
    ) {
        Long uid = userSupport.getCurrentUser().getId();
        List<UserMessage> privateMessageList = userMessageRepository.getPrivateMessageList(uid);
        return privateMessageList;
    }

    @PutMapping("/send")
    public String sendMessage(@Validated @RequestBody
            SendPrivateMessageRB sendPrivateMessageRB) throws Exception {
        if (userPrivateMessageService.sendPrivateMessage(sendPrivateMessageRB)) {
            return "发送成功";
        } else {
            return "发送失败";
        }
    }

    @GetMapping("/pull/{toUserId}")
    public PageData<UserMessage> pullMessageByLatestId(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page,
            @RequestParam(name ="latest") long latestId, @PathVariable long toUserId) {

        PageData<UserMessage> userMessagePageData = userPrivateMessageService
                .pullMessageByLatestId(latestId, toUserId, page < 0 ? 0 : page-1, Math.min(size, 20));
        return userMessagePageData;
    }

    @GetMapping("pull/history/{toUserId}")
    public PageData<UserMessage> pullHistoryMessage(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page,
            @RequestParam(name ="latest") long latestId, @PathVariable long toUserId) {

        PageData<UserMessage> userMessagePageData = userPrivateMessageService
                .pullHistoryMessage(latestId, toUserId, page, size);
        return userMessagePageData;
    }
}
