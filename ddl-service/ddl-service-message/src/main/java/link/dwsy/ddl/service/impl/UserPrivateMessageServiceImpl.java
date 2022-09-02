package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import link.dwsy.ddl.service.UserPrivateMessageService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@Service
public class UserPrivateMessageServiceImpl implements UserPrivateMessageService {
    @Resource
    UserMessageRepository userMessageRepository;
    @Resource
    UserSupport userSupport;

    @Override
    public boolean sendPrivateMessage(SendPrivateMessageRB sendPrivateMessageRB)  {
        long formUserId = userSupport.getCurrentUser().getId();
        long toUserId = sendPrivateMessageRB.getToUserId();
        String content = sendPrivateMessageRB.getContent();
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
        try {
            userMessageRepository.save(message);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public PageData<UserMessage> pullMessageByLatestId(long latestId, long toUserId, int page,int size)  {
        long formUserId = userSupport.getCurrentUser().getId();
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
                .findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn(conversationId, latestId, messageStates, pr);
        return new PageData<>(messages);
    };

    public PageData<UserMessage> pullHistoryMessage(long latestId, long toUserId, int page, int size) {
        long formUserId = userSupport.getCurrentUser().getId();
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

        return new PageData<>(messages);
    }
}

