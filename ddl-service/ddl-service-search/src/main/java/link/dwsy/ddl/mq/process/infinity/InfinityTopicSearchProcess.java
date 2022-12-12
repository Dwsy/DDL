package link.dwsy.ddl.mq.process.infinity;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import link.dwsy.ddl.XO.ES.Infinity.InfinityTopicEsDoc;
import link.dwsy.ddl.constants.mq.InfinityTopicMQConstants;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */
@Service
@Slf4j
public class InfinityTopicSearchProcess {
    private final String INDEX = InfinityTopicMQConstants.INDEX;
    @Resource
    private ElasticsearchClient client;

    @Resource
    private InfinityTopicRepository infinityTopicRepository;

    public void updateOrSaveAllDataById(long topicId) {
        infinityTopicRepository.findById(topicId).ifPresent(topic -> {
            InfinityTopicEsDoc topicEsDoc = InfinityTopicEsDoc.builder()
                    .id(topic.getId())
                    .name(topic.getName())
                    .viewNum(topic.getViewNum())
                    .infinityNum(topic.getInfinityNum())
                    .build();
            try {
                client.update(req -> req
                                .index(INDEX).id(String.valueOf(topicId))
                                .doc(topicEsDoc).docAsUpsert(true)
                        , InfinityTopicEsDoc.class);
            } catch (Exception e) {
                log.info("更新失败 topicId ：{}", topicId);
            }
        });

    }
}
