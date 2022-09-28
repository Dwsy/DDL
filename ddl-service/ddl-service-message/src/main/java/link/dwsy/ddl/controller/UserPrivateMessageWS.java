package link.dwsy.ddl.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.WS.Constants.UserPrivateMessageConstants;
import link.dwsy.ddl.XO.WS.Message.UserPrivateWsMessage;
import link.dwsy.ddl.XO.WS.WsSession.UserPrivateWsMessageSession;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/28
 */

@Component
@ServerEndpoint("/private/message/{conversationId}")
@Slf4j
@SuppressWarnings("all")
public class UserPrivateMessageWS {
    private static UserSupport userSupport;

    private static RedisMessageListenerContainer redisMessageListenerContainer;

    private static StringRedisTemplate stringRedisTemplate;

    private static UserRepository userRepository;

    private static ConcurrentHashMap<String, UserPrivateWsMessageSession> SessionMapSet = new ConcurrentHashMap<>();

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        UserPrivateMessageWS.objectMapper = objectMapper;
    }

    @Autowired
    public void setUserSupport(UserSupport userSupport) {
        UserPrivateMessageWS.userSupport = userSupport;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        UserPrivateMessageWS.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setRedisMessageListenerContainer(RedisMessageListenerContainer redisMessageListenerContainer) {
        UserPrivateMessageWS.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        UserPrivateMessageWS.userRepository = userRepository;
    }
//    small

    @OnOpen
    public void onOpen(@PathParam("conversationId") String conversationId, Session session) throws IOException {

        List<Integer> s = Arrays.stream(conversationId.split("_")).map(Integer::valueOf).collect(Collectors.toList());
        if (s.get(0) >= s.get(1)) {
            session.getBasicRemote().sendText("error");
            session.close();
        } else {
            if (!userRepository.existsById(Long.valueOf(s.get(0)))) {
                session.getBasicRemote().sendText("error");
                session.close();
            }
            if (!userRepository.existsById(Long.valueOf(s.get(1)))) {
                session.getBasicRemote().sendText("error");
                session.close();
            }
            if (!SessionMapSet.containsKey(conversationId)) {
                SessionMapSet.put(conversationId, UserPrivateWsMessageSession.builder()
                        .SmallId(s.get(0)).BigId(s.get(1))
                        .SmallIdUserSessionSet(new HashSet<>())
                        .BigIdUserSessionSet(new HashSet<>())
                        .build());
            }
            session.getBasicRemote().sendText("success");
        }
    }


    @OnMessage
    public void onMessage(@PathParam("conversationId") String conversationId, String message, Session session) throws IOException {
        UserPrivateWsMessageSession sms = SessionMapSet.get(conversationId);
        if (sms == null) {
            session.getBasicRemote().sendText("error");
            return;
        }
        UserPrivateWsMessage wsMessage = null;
        LoginUserInfo loginUserInfo = null;
        try {
            wsMessage = JSONObject.parseObject(message, UserPrivateWsMessage.class);
        } catch (JSONException e) {
            session.getBasicRemote().sendText("消息格式错误");
            return;
        }
        int type = wsMessage.getType();
        if (type == 0) {
            String token = wsMessage.getContent();
            if (StrUtil.isBlank(token)) {
                session.getBasicRemote().sendText("鉴权失败");
            }
            try {
                loginUserInfo = TokenUtil.parseUserInfoFromToken(token);
            } catch (Exception e) {
                session.getBasicRemote().sendText("token错误");
                return;
            }
            if (loginUserInfo.getId() == sms.getSmallId()) {
                sms.getSmallIdUserSessionSet().add(session);
            } else {
                if (loginUserInfo.getId() == sms.getBigId()) {
                    sms.getBigIdUserSessionSet().add(session);
                } else {
                    session.getBasicRemote().sendText("token错误");
                    session.close();
                    return;
                }
            }
            if (!sms.isHasSubChannel()) {
                redisMessageListenerContainer.addMessageListener((msg, bytes) -> {
                    try {
                        this.sendMessageAll(conversationId, msg.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, new ChannelTopic(UserPrivateMessageConstants.RedisChannelTopic + conversationId));
                sms.setHasSubChannel(true);
            }
            session.getBasicRemote().sendText("鉴权成功");
        }

    }

    public void sendMessageAll(String conversationId, String message) throws IOException {
        boolean toBig;
        UserMessage privateWsMessage = (UserMessage) objectMapper.readValue(message, UserMessage.class);
        UserPrivateWsMessageSession sms = SessionMapSet.get(conversationId);
        if (sms != null) {
            if (privateWsMessage.getToUserId() == sms.getBigId()) {
                toBig = true;
            } else {
                toBig = false;
            }
            Set<Session> sessionHashSet;
            if (toBig) {
                sessionHashSet = sms.getBigIdUserSessionSet();
            } else {
                sessionHashSet = sms.getSmallIdUserSessionSet();
            }
            System.out.println(sessionHashSet.size());
            for (Session session : sessionHashSet) {
                log.info("sendTo:{}",privateWsMessage.getToUserId());
                synchronized (session) {
                    session.getBasicRemote().sendText(message);
                }
            }
        } else {
            log.info("null");
        }

    }

    @OnClose
    public void onClose(@PathParam("conversationId") String conversationId, Session session) {
        UserPrivateWsMessageSession sms = SessionMapSet.get(conversationId);
        if (sms.getBigIdUserSessionSet().contains(session)) {
            sms.getBigIdUserSessionSet().remove(session);
        } else {
            sms.getSmallIdUserSessionSet().remove(session);
        }
        log.info("removeSession{}",session);
    }

    @OnError
    public void error(Throwable throwable) {
        try {
            throw throwable;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
