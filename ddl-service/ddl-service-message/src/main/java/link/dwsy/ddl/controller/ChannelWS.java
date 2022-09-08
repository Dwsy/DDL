package link.dwsy.ddl.controller;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import link.dwsy.ddl.Util.RedisUtil;
import link.dwsy.ddl.XO.RB.ChannelWsMessage;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.repository.Meaasge.ChannelRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author Dwsy
 * @Date 2022/9/3
 */
@Component
@ServerEndpoint("/channel/{id}")
@Slf4j
@SuppressWarnings("all")
public class ChannelWS {

//    private static final AtomicInteger onlineUserCount = new AtomicInteger(0);


    private static ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> ChannelSocket = new ConcurrentHashMap<>();
    private static RedisUtil redisUtil;
    private static RedisTemplate redisTemplate;

    private static StringRedisTemplate stringRedisTemplate;
    private static UserSupport userSupport;
    private static ChannelRepository channelRepository;
    private static RedisMessageListenerContainer redisMessageListenerContainer;
    private static HashMap<String,LoginUserInfo> currentUserMap=new HashMap<>();


    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        ChannelWS.redisUtil = redisUtil;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        ChannelWS.stringRedisTemplate = stringRedisTemplate;
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        ChannelWS.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setUserSupport(UserSupport userSupport) {
        ChannelWS.userSupport = userSupport;
    }

    @Autowired
    public void setChannelRepository(ChannelRepository channelRepository) {
        ChannelWS.channelRepository = channelRepository;
    }

    @Autowired
    public void setRedisMessageListenerContainer(RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }


    @OnOpen
    public void open(@PathParam("id") Long id, Session session) throws IOException {
        if (!channelRepository.findById(id).isPresent()) {
            session.getBasicRemote().sendText("cid ont found");
            session.close();
        }

        session.getBasicRemote().sendText("success->continue");
    }

    @OnClose
    public void close(@PathParam("id") Long id, Session session) throws IOException {
        CopyOnWriteArraySet<Session> channelWS = ChannelSocket.get(id);
        LoginUserInfo loginUserInfo = currentUserMap.get(session.getId());
        if (loginUserInfo != null) {
            redisTemplate.opsForHash().increment("channel:user:hash"+id, loginUserInfo.getId().toString(), -1);

            Long remove = redisTemplate.opsForValue().decrement("channel:online:user" + id);
            if (remove == 0) {
                redisTemplate.opsForSet().remove("channel:user:id" + id, loginUserInfo.getId().toString());
                log.info("用户{}退出频道{}，当前频道在线人数{}", loginUserInfo.getUsername(), id, remove);
            }

        } else {
            redisTemplate.opsForValue().increment("channel:send:message" + id, -1);
            log.info("游客退出");
        }
        channelWS.remove(session);
    }

    @OnMessage
    public void receiveMessage(@PathParam("id") Long id, Session session, String m) throws IOException {
        ChannelWsMessage wsMessage = null;
        LoginUserInfo loginUserInfo = null;
        try {
            wsMessage = JSONObject.parseObject(m, ChannelWsMessage.class);
        } catch (JSONException e) {
            session.getBasicRemote().sendText("消息格式错误");
            return;
        }
        int type = wsMessage.getType();
        if (type == 0) {
            if (currentUserMap.get(session.getId())!=null) {
                session.getBasicRemote().sendText("已鉴权");
                return;
            }
            String token = wsMessage.getContent();
            if (token != null) {

                try {
                    loginUserInfo = TokenUtil.parseUserInfoFromToken(token);
                }catch (Exception e){
                    session.getBasicRemote().sendText("token错误");
                    return;
                }

                this.currentUserMap.put(session.getId(), loginUserInfo);
                CopyOnWriteArraySet<Session> channelWS = ChannelSocket.get(id);
//                todo 鉴权
                if (ChannelSocket.containsKey(id)) {
                    channelWS.add(session);
                } else {
                    channelWS = new CopyOnWriteArraySet<>();
                    channelWS.add(session);
                    ChannelSocket.put(id, channelWS);
                }
                redisTemplate.opsForHash().increment("channel:user:hash"+id, loginUserInfo.getId().toString() + loginUserInfo.getId(), 1L);
                if (redisTemplate.opsForSet().add("channel:user:id"+id, loginUserInfo.getId().toString()) > 0) {
                    Long increment = redisUtil.increment("channel:online:user" + id, 1);
                    log.info("用户{}加入频道{}，当前频道在线人数{}", loginUserInfo.getId(), id,increment);
                }
                redisMessageListenerContainer.addMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message, byte[] bytes) {
                        try {
                            ChannelWS.this.sendMessageAll(id, message.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ChannelTopic("channel:message" + id));
                session.getBasicRemote().sendText("鉴权成功");
            } else {
                session.getBasicRemote().sendText("鉴权失败");

            }

        }
        if (type == 1) {
            log.info("游客登录当前数量{}", redisTemplate.opsForValue().increment("channel:send:message" + id, 1));
            redisMessageListenerContainer.addMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] bytes) {
                    try {
                        ChannelWS.this.sendMessageAll(id, message.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new ChannelTopic("channel:message" + id));
            CopyOnWriteArraySet<Session> channelWS = ChannelSocket.get(id);
            if (ChannelSocket.containsKey(id)) {
                channelWS.add(session);
            } else {
                channelWS = new CopyOnWriteArraySet<>();
                channelWS.add(session);
                ChannelSocket.put(id, channelWS);
            }
            session.getBasicRemote().sendText("游客登录成功");
        }
//        if (type == 3) {
//            LoginUserInfo userInfo = currentUserMap.get(session.getId());
//            if (userInfo== null) {
//                session.getBasicRemote().sendText("游客无法发送消息");
//                return;
//            }
//            ChannelMessageVo vo = ChannelMessageVo.builder()
//                    .userid(userInfo.getId())
//                    .channel(id).username(userInfo.getNickname())
//                    .message(wsMessage.getContent()).build();
//            stringRedisTemplate.convertAndSend("channel:message"+ id, vo);
//        }
    }

    @OnError
    public void error(Throwable throwable) {
        try {
            throw throwable;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public  void  sendMessageAll(Long cid, String message) throws IOException {
        CopyOnWriteArraySet<Session> channelWS = ChannelSocket.get(cid);
        if (channelWS != null) {

            for (Session session1 : channelWS) {
                synchronized (session1) {
                    session1.getBasicRemote().sendText(message);
                }

            }
        } else {
            log.info("null");
        }

    }

}
