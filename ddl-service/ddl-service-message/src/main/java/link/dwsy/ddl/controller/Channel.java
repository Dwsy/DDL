package link.dwsy.ddl.controller;

import com.alibaba.fastjson2.JSONObject;
import link.dwsy.ddl.XO.RB.channelSendMessageRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Message.ChannelMessage;
import link.dwsy.ddl.repository.Meaasge.ChannelMessageRepository;
import link.dwsy.ddl.repository.Meaasge.ChannelRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */
@RestController
@RequestMapping("/channel")
public class Channel {
    @Resource
    ChannelWS channelWS;
    @Resource
    ChannelRepository channelRepository;
    @Resource
    ChannelMessageRepository channelMessageRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserSupport userSupport;
    @GetMapping("/list")
    public Object list() {
        return channelRepository.findAll();
    }

    @PostMapping("/send")
    @AuthAnnotation(Level = 1)
    public void sendMessage(@RequestBody channelSendMessageRB channelSendMessageRB) {
        LoginUserInfo user = userSupport.getCurrentUser();
        ChannelMessage message = ChannelMessage.builder()
                .userId(user.getId())
                .message(channelSendMessageRB.getMessage())
                .channelId(channelSendMessageRB.getChannelId()).build();
        stringRedisTemplate.convertAndSend("channel:message"+channelSendMessageRB.getChannelId(), JSONObject.toJSONString(message));
        channelMessageRepository.save(message);
    }
    }

