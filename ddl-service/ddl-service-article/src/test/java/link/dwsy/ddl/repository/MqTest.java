package link.dwsy.ddl.repository;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.constants.mq.ArticleSearchMQConstants;
import link.dwsy.ddl.constants.mq.UserPointsMQConstants;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@SpringBootTest
public class MqTest {
    @Resource
    ArticleFieldServiceImpl articleFieldService;
    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Resource
    ArticleContentRepository articleContentRepository;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Test
    public void TEST1() {
        articleFieldService.logicallyDeleted(9L);
        articleFieldService.logicallyDeleted(9L);
        articleFieldService.logicallyDeleted(9L);
        articleFieldService.logicallyDeleted(9L);
    }

    @Test
    public void TEST2() {
        var aid = 9L;
        articleFieldRepository.logicallyRecovery(aid);
        articleContentRepository.logicallyRecovery(aid);
        rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_CREATE, aid);
    }

    @Test
    public void TEST3() {
        var userActiveType = UserActiveType.Browse_Article;
        UserActiveMessage build = UserActiveMessage.builder()
                .userActiveType(userActiveType).userId(3L)
                .sourceId(9L).build();
        rabbitTemplate.convertAndSend("history.user.active", build);
    }

    @Test
    public void TEST$() {
        PointsType reward = PointsType.Reward;
        UserPointsMessage message = UserPointsMessage.builder().pointsType(reward).point(1.1f).userId(3L).build();
        rabbitTemplate.convertAndSend(UserPointsMQConstants.QUEUE_DDL_USER_POINTS, message);
    }
}
