package link.dwsy.ddl.mq.listener.infinity.topic;

import link.dwsy.ddl.constants.mq.InfinityTopicMQConstants;
import link.dwsy.ddl.mq.process.infinity.InfinityTopicSearchProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

@Component
@Slf4j
public class TopicSearch {

    @Resource
    private InfinityTopicSearchProcess infinityTopicSearchProcess;

    @RabbitListener(queues = {
            InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_UPDATE,
            InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_CREATE
    })
    public void updateAllDataById(Long topicId) {
        if (topicId != null && topicId > 0) {
            infinityTopicSearchProcess.updateOrSaveAllDataById(topicId);
            log.info("topic doc:{}新增或更新成功", topicId);
        }
    }
}
