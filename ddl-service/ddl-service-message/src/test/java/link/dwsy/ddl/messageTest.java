package link.dwsy.ddl;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.HashSet;

/**
 * @Author Dwsy
 * @Date 2022/9/1
 */
@SpringBootTest
public class messageTest {

    @Resource
    private UserMessageRepository userMessageRepository;

    @Test
    public void test() {
        sendMessage(3L, 4L,"hello1");
        sendMessage(4L, 3L,"hello hello1");
        sendMessage(3L, 4L,"hello2");
        sendMessage(4L, 3L,"hello hello2");
        sendMessage(3L, 4L,"hello3");
        sendMessage(4L, 3L,"hello hello3");
        sendMessage(3L, 4L,"hello4");
        sendMessage(4L, 3L,"hello hello4");
    }

    @Test
    public void test2() {
        pullMessageByLatestId(3L, 4L, 3L);
        pullMessageByLatestId(3L, 4L, 5L);
        pullMessageByLatestId(3L, 4L, 6L);
    }

    @Test
    void test3() {
        pullHistoryMessage(3L, 4L, 10L,5,0);
        pullHistoryMessage(3L, 4L, 10L,5,1);
    }
    public void sendMessage(long formUserId, long toUserId,String content) {
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

    public void pullMessageByLatestId (long formUserId, long toUserId,long latestId) {
        String conversationId;
        if (formUserId<toUserId)
            conversationId = formUserId + "_" + toUserId;
        else
            conversationId = toUserId + "_" + formUserId;
        HashSet<MessageState> messageStates = new HashSet<>();
        messageStates.add(MessageState.READ);
        messageStates.add(MessageState.UNREAD);
        PageRequest pr = PageRequest.of(0, 20);
        Page<UserMessage> messages = userMessageRepository
                .findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn(conversationId, latestId, messageStates, pr);
        messages.forEach(System.out::println);
    }

//    反向分页查询
    public void pullHistoryMessage(long formUserId, long toUserId, long latestId,int size,int page) {
        String conversationId;
        if (formUserId<toUserId)
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



}
