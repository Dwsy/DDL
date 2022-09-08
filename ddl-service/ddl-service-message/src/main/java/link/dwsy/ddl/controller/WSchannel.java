//package link.dwsy.ddl.controller;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import link.dwsy.ddl.Util.RedisUtil;
//import link.dwsy.ddl.core.domain.LoginUserInfo;
//import link.dwsy.ddl.repository.Meaasge.ChannelRepository;
//import link.dwsy.ddl.support.UserSupport;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CopyOnWriteArraySet;
//
///**
// * @Author Dwsy
// * @Date 2022/9/3
// */
//@Component
//@ServerEndpoint("/channel/{id}")
//@Slf4j
//public class WSchannel {
//
////    private static final AtomicInteger onlineUserCount = new AtomicInteger(0);
//
//
//    @Resource
//    RedisUtil redisUtil;
//
//    @Resource
//    RedisTemplate redisTemplate;
//
//    @Resource
//    ChannelRepository channelRepository;
//
//    @Resource
//    UserSupport userSupport;
//
//    private LoginUserInfo currentUser;
//
//    private Session session;
//    private static CopyOnWriteArraySet<WSchannel> webSocketSet = new CopyOnWriteArraySet<>();
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @OnOpen
//    public void open(@PathParam("id") String id, Session session) throws IOException {
//        if (channelRepository.findById(Long.parseLong(id)).isPresent()) {
//            this.session = session;
////            this.currentUser = userSupport.getCurrentUser();
//            redisTemplate.opsForValue().increment("channel_online_" + id);
////            redisTemplate.opsForSet().add("channel_online_user_" + id, currentUser.getId());
//            redisTemplate.opsForHash().increment("channel_online_user" + id, currentUser.getId(), 1);
//            webSocketSet.add(this);
//            log.info("用户{}加入频道{}", currentUser.getId(), id);
//            sendMessageAll(objectMapper.writeValueAsString(this.currentUser));
//        } else {
//            session.getBasicRemote().sendText("未登录");
////            session.close();
//        }
//    }
//
//    @OnClose
//    public void close(@PathParam("id") String id, Session session) {
//        // 如果某个用户离开了，就移除相应的信息
//        Long increment = redisTemplate.opsForHash().increment("channel_online_user" + id, currentUser.getId(), -1);
//        if (increment >= 0) {
//            redisTemplate.opsForValue().increment("channel_online_" + id, -1);
//            log.info("用户{}退出聊天,房间人数:{}", session.getId(), increment);
//        }
//        webSocketSet.remove(this);
//    }
//
//    @OnMessage
//    public void reviveMessage(@PathParam("id") String id, Session session, String message) throws IOException {
//        log.info("【websocket消息】收到客户端发来的消息:{}", message);
//        JSONObject jsonObject = JSON.parseObject(message);
//        Object messageValue = objectMapper.readValue(message, Object.class);
//        //获取所有数据
//        String textMessage = jsonObject.getString("message");
//        String username = jsonObject.getString("username");
//        String type = jsonObject.getString("type");
//        String tousername = jsonObject.getString("tousername");
//        Map<String, Object> map3 = new HashMap();
//        map3.put("messageType", 4);
//        //所有在线用户
//        map3.put("onlineUsers", this.webSocketSet);
//        //发送消息的用户名
//        map3.put("username", username);
//        //返回在线人数
//        map3.put("number", redisTemplate.opsForValue().get("channel_online_" + id));
//        //发送的消息
//        map3.put("textMessage", textMessage);
//
//        sendMessageAll(JSON.toJSONString(map3));
//    }
//
//    @OnError
//    public void error(Throwable throwable) {
//        try {
//            throw throwable;
//        } catch (Throwable e) {
//            log.error("未知错误");
//        }
//    }
//
//    public void sendMessageAll(String message) throws IOException {
//        for (WSchannel webSocket : webSocketSet) {
//            //消息发送所有人（同步）getAsyncRemote
//            webSocket.session.getBasicRemote().sendText(message);
//        }
//    }
//
//    public static void sendMessage(String message) {
//        for (WSchannel webSocket: webSocketSet) {
//            log.info("【websocket消息】广播消息, message={}", message);
//            try {
//                webSocket.session.getBasicRemote().sendText(message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//}
