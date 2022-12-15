package link.dwsy.ddl;

import link.dwsy.ddl.constants.mq.InfinityTopicMQConstants;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/22
 */
@SpringBootTest
public class TEST {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private InfinityTopicRepository infinityTopicRepository1;

    @Resource
    private InfinityRepository infinityRepository;

    @Test
    public void Test() {
        List<InfinityTopic> all = infinityTopicRepository1.findAll();
        all.forEach(infinityTopic -> {
            rabbitTemplate.convertAndSend(InfinityTopicMQConstants.EXCHANGE_DDL_INFINITY_TOPIC, InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_UPDATE, infinityTopic.getId());
        });;
    }

    @Test
    public void Test1() {
        infinityRepository.TopicIdsAndClubId(1602338399681773568L);
    }
}
