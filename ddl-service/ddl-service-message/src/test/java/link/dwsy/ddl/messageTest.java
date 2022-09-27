package link.dwsy.ddl;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.HashSet;

/**
 * @Author Dwsy
 * @Date 2022/9/1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// todo websocket 需要
public class messageTest {

    @Resource
    private UserMessageRepository userMessageRepository;

    @Test
    public void sout() {
        System.out.println(123);
    }

    @Test
    public void test() {
        sendMessage(3L, 5L, "3->5");
        sendMessage(3L, 6L, "3->6");

        sendMessage(6L, 3L, "6->3");
    }

    @Test
    public void test2() {
        pullMessageByLatestId(3L, 4L, 0L);
//        pullMessageByLatestId(3L, 4L, 5L);
//        pullMessageByLatestId(3L, 4L, 6L);
    }

    @Test
    void test3() {
        pullHistoryMessage(3L, 4L, 10L, 5, 0);
        pullHistoryMessage(3L, 4L, 10L, 5, 1);
    }

    @Test
    void test4() {
        getPrivateMessageList(3L);
//        getPrivateMessageList(4L);
//        getPrivateMessageList(6L);
    }

    public void sendMessage(long formUserId, long toUserId, String content) {
        String conversationId;
        if (formUserId < toUserId) {
            conversationId = formUserId + "_" + toUserId;
        } else {
            conversationId = toUserId + "_" + formUserId;
        }
        UserMessage message = UserMessage.builder()
                .formUserId(formUserId)
                .toUserId(toUserId)
                .content(content)
                .conversationId(conversationId).build();
        userMessageRepository.save(message);
    }

    public void pullMessageByLatestId(long formUserId, long toUserId, long latestId) {
        String conversationId;
        if (formUserId < toUserId)
            conversationId = formUserId + "_" + toUserId;
        else
            conversationId = toUserId + "_" + formUserId;
        HashSet<MessageState> messageStates = new HashSet<>();
        messageStates.add(MessageState.READ);
        messageStates.add(MessageState.UNREAD);
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        PageRequest pr = PageRequest.of(0, 2,sort);
        Page<UserMessage> messages = userMessageRepository
                .findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn(conversationId, latestId, messageStates, pr);
        messages.forEach(System.out::println);
    }

    //    反向分页查询
    public void pullHistoryMessage(long formUserId, long toUserId, long latestId, int size, int page) {
        String conversationId;
        if (formUserId < toUserId)
            conversationId = formUserId + "_" + toUserId;
        else
            conversationId = toUserId + "_" + formUserId;
        HashSet<MessageState> messageStates = new HashSet<>();
        messageStates.add(MessageState.READ);
        messageStates.add(MessageState.UNREAD);
        PageRequest pr = PageRequest.of(page, size);
        Page<UserMessage> messages = userMessageRepository
                .findByConversationIdAndIdLessThanAndDeletedFalseAndStatusIn(conversationId, latestId, messageStates, pr);
        messages.forEach(System.out::println);
    }

    public void getPrivateMessageList(long uid) {
//        String conversationId;
//        if (formUserId<toUserId)
//            conversationId = formUserId + "_" + toUserId;
//        else
//            conversationId = toUserId + "_" + formUserId;
//        HashSet<MessageState> messageStates = new HashSet<>();
//        messageStates.add(MessageState.READ);
//        messageStates.add(MessageState.UNREAD);

        Page<UserMessage> ttt = userMessageRepository.getPrivateMessageListPage(uid,PageRequest.of(0,2));
        ttt.forEach(System.out::println);

    }

}
