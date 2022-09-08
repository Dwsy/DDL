package link.dwsy.ddl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.entity.Message.Channel;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Meaasge.ChannelRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@SpringBootTest
public class channelTest {

    @Resource
    ChannelRepository channelRepository;

    @Resource
    UserRepository userRepository;

    @Test
    public void test() {
        System.out.println(channelRepository.findAll());
    }

    @Test
    public void addChannel() {
        Channel channel = Channel.builder().cover("http://www.itcast.cn/files/image/201912/20191231102611263.jpg")
                .description("Java xxx")
                .isReadOnly(false)
                .isPublic(true)
                .name("Java").build();
        Channel channel2 = Channel.builder().cover("https://picx.zhimg.com/v2-839798432500b3aec901cba0efb93bf7_1440w.jpg?source=172ae18b")
                .description("Rust xxx")
                .isReadOnly(false)
                .isPublic(true)
                .name("Rust").build();

        channelRepository.save(channel);
        channelRepository.save(channel2);
    }

    @Test
    public void addUser() {
        if (channelRepository.existsUser(4L, 6L) == 0) {
            channelRepository.addUser(4L, 6L);
        } else {
            System.out.println("已经存在");
        }

        try {
            int i = channelRepository.addUser(4L, 6L);
            System.out.println(i);
        } catch (Exception e) {
            System.out.println("----------");
            System.out.println(e);
        }
//        channelRepository.addUser(1L,1L);
    }

    @Test
    public void exitChannel() {
        int i = channelRepository.existsUser(4L, 6L);
        if (i == 1) {
            if (channelRepository.exitChannel(4L, 6L) == 1) {
                System.out.println("退出成功");
            }

        }else {
            System.out.println("不存在");
        }
    }

    @Test
    public void findUserChannel() throws JsonProcessingException {
        List<Long> userChannel = channelRepository.findUserChannel(3L);
        List<Long> userChannel1 = channelRepository.findUserChannel(4L);
        List<Channel> allById = channelRepository.findAllById(userChannel);
        List<Channel> allById1 = channelRepository.findAllById(userChannel1);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(allById));
        System.out.println(objectMapper.writeValueAsString(allById1));
    }

    @Test
    public void findChannelUser() throws JsonProcessingException {
        List<Long> userIdList = channelRepository.findChannelUser(1L);
        List<User> allByIdAndDeletedIsFalse = userRepository.findByIdInAndDeletedFalse(userIdList);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(allByIdAndDeletedIsFalse));
    }

    @Resource
    private RedisTemplate redisTemplate;
    @Test
    public void redis() {
        System.out.println(redisTemplate.opsForSet().add("channel:1:user", 1));
        System.out.println(redisTemplate.opsForSet().add("channel:1:user", 1));
        System.out.println(redisTemplate.opsForSet().add("channel:1:user", 1));
    }
}
