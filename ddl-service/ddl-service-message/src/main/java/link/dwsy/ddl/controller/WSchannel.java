//package link.dwsy.ddl.controller;
//
//import com.alibaba.fastjson2.JSON;
//import link.dwsy.ddl.Util.RedisUtil;
//import link.dwsy.ddl.repository.Meaasge.ChannelRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import springfox.documentation.spring.web.json.Json;
//
//import javax.annotation.Resource;
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
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
//
//    @OnOpen
//    public void open(@PathParam("id") String id, Session session) throws IOException {
//        channelRepository.findById(Long.parseLong(id)).ifPresent(channel -> {
//            redisUtil.increment("onlineUserCount",1);
//            redisUtil.increment("channel" + "UserCount_" + id, 1);
//            String o = (String) redisUtil.get("channel" + "UserIdList_" + id);
////            redisUtil.get("channel" + "UserSession_" + id + "_" );
//            if (o != null) {
//                List list = JSON.parseObject(o, List.class);
//                list.add(session.getId());
//                redisUtil.set("channel" + "UserIdList_" + id, JSON.toJSONString(list));
//                redisUtil.set("channel" + "UserSession_" + id, JSON.toJSONString(session));
//                redisUtil.set("channel" + "Session_" + id, JSON.toJSONString(list));
//            }else {
//                redisUtil.set("channel" + "UserIdList_" + id, JSON.toJSONString(List.of(session.getId())));
//            }
//
//        });
//    }
//
//    @OnClose
//    public void close(@PathParam("id") String id, Session session){
//        // 如果某个用户离开了，就移除相应的信息
//        List list = JSON.parseObject((String) redisUtil.get("channel" + "UserIdList_" + id), List.class);
//        if(list.contains(session.getId())){
//            list.remove(session.getId());
//            redisUtil.set("channel" + "UserIdList_" + id, JSON.toJSONString(list));
//        }
//        // 房间人数-1
//        redisUtil.increment("onlineUserCount",-1);
//        Long increment = redisUtil.increment("channel" + "UserCount_" + id, -1);
//        log.info("用户{}退出聊天,房间人数:{}",session.getId(),increment);
//    }
//
//    @OnMessage
//    public void reveiveMessage(@PathParam("id") String page, Session session,String message) throws IOException {
//        log.info("接受到用户{}的数据:{}",session.getId(),message);
//        // 拼接一下用户信息
//        String msg = session.getId()+" : "+ message;
//        Set<Session> sessions = roomMap.get(page);
//        // 给房间内所有用户推送信息
//        for(Session s : sessions){
//            s.getBasicRemote().sendText(msg);
//        }
//    }
//
//    @OnError
//    public void error(Throwable throwable){
//        try {
//            throw throwable;
//        } catch (Throwable e) {
//            log.error("未知错误");
//        }
//    }
//}
