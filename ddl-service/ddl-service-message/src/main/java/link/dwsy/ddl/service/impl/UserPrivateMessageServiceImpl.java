package link.dwsy.ddl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;
import link.dwsy.ddl.XO.WS.Constants.UserPrivateMessageConstants;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Meaasge.UserMessageRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.UserPrivateMessageService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Resource
    UserRepository userRepository;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    ObjectMapper objectMapper;

    @Override
    public boolean sendPrivateMessage(SendPrivateMessageRB sendPrivateMessageRB) {
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
            UserMessage save = userMessageRepository.save(message);
            String msgStr = objectMapper.writeValueAsString(save);
            stringRedisTemplate.convertAndSend(UserPrivateMessageConstants.RedisChannelTopic + conversationId, msgStr);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public PageData<UserMessage> pullMessageByLatestId(long latestId, long toUserId, PageRequest pr) {
        long formUserId = userSupport.getCurrentUser().getId();
        if (formUserId == toUserId) {
            return null;
        }
        String conversationId;
        if (formUserId < toUserId)
            conversationId = formUserId + "_" + toUserId;
        else
            conversationId = toUserId + "_" + formUserId;
        HashSet<MessageState> messageStates = new HashSet<>();
        messageStates.add(MessageState.READ);
        messageStates.add(MessageState.UNREAD);
        Page<UserMessage> messages = userMessageRepository
                .findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn(conversationId, latestId, messageStates, pr);
        User toUser = userRepository.findUserByIdAndDeletedIsFalse(toUserId);
        for (UserMessage message : messages.getContent()) {
            message.setChatUserNickname(toUser.getNickname());
            message.setChatUserId(toUser.getId());
            message.setChatUserAvatar(toUser.getUserInfo().getAvatar());
        }
        return new PageData<>(messages);
    }

    ;

    public PageData<UserMessage> pullHistoryMessage(long latestId, long toUserId, int page, int size) {
        long formUserId = userSupport.getCurrentUser().getId();
        if (formUserId == toUserId) {
            return null;
        }
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
        //todo 优化
        User toUser = userRepository.findUserByIdAndDeletedIsFalse(toUserId);
        for (UserMessage message : messages.getContent()) {
            message.setChatUserNickname(toUser.getNickname());
            message.setChatUserId(toUser.getId());
            message.setChatUserAvatar(toUser.getUserInfo().getAvatar());
        }
        return new PageData<>(messages);
    }
}

