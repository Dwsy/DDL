package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.InfinityTopicRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.constants.mq.InfinityTopicMQConstants;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@RestController
@RequestMapping("topic")
@Slf4j
public class InfinityTopicController {

    @Resource
    private UserSupport userSupport;

    @Resource
    private InfinityRepository infinityRepository;

    @Resource
    private InfinityTopicRepository infinityTopicRepository;

    @Resource
    private InfinityClubRepository infinityClubRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping
    @AuthAnnotation
    public InfinityTopic createInfinityTopic(@Validated @RequestBody InfinityTopicRB infinityTopicRB) {
        Long userId = userSupport.getCurrentUser().getId();
        if (infinityTopicRepository.existsByDeletedFalseAndName(infinityTopicRB.getName())) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_EXIST);
        }
        InfinityTopic infinityTopic = new InfinityTopic();
        infinityTopic.setCreateUser((User) new User().setId(userId));
        infinityTopic.setName(infinityTopicRB.getName());
        infinityTopic.setCover(infinityTopicRB.getCover());
        infinityTopic.setDescription(infinityTopicRB.getDescription());
        infinityTopic.setNotice(infinityTopicRB.getNotice());
        InfinityTopic save = infinityTopicRepository.save(infinityTopic);
        rabbitTemplate.convertAndSend(InfinityTopicMQConstants.EXCHANGE_DDL_INFINITY_TOPIC,
                InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_CREATE, save.getId());
        return save;
    }

    @PutMapping
    @AuthAnnotation
    public InfinityTopic updateInfinityTopic(@Validated @RequestBody InfinityTopicRB infinityTopicRB) {
        Long userId = userSupport.getCurrentUser().getId();
        Long topicId = infinityTopicRB.getId();
        if (topicId == null) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        InfinityTopic infinityTopic = infinityTopicRepository.findById(topicId)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
        if (infinityTopic.getCreateUser().getId() != userId) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        infinityTopic.setName(infinityTopicRB.getName());
        infinityTopic.setCover(infinityTopicRB.getCover());
        infinityTopic.setDescription(infinityTopicRB.getDescription());
        infinityTopic.setNotice(infinityTopicRB.getNotice());
        InfinityTopic save = infinityTopicRepository.save(infinityTopic);
        rabbitTemplate.convertAndSend(InfinityTopicMQConstants.EXCHANGE_DDL_INFINITY_TOPIC,
                InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_UPDATE, save.getId());
        return save;
    }

    @DeleteMapping("{topicId}")
    @AuthAnnotation(Level = 999)
    public void deleteInfinityTopic(@PathVariable Long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        InfinityTopic infinityTopic = infinityTopicRepository.findById(topicId)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
        if (infinityTopic.getCreateUser().getId() != userId) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        if (infinityTopic.getFollowerNum() > 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        } else {
            infinityTopic.setDeleted(true);
            infinityTopicRepository.save(infinityTopic);
        }
    }

    @PostMapping("follow/{topicId}")
    @AuthAnnotation
    public boolean followInfinityTopic(@PathVariable long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityTopicRepository.existsByDeletedAndId(false, topicId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        if (infinityTopicRepository.isFollow(topicId, userId) > 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_FOLLOWED);
        }
        infinityTopicRepository.followerNumIncrement(topicId, 1);
        return infinityTopicRepository.followTopic(topicId, userId) > 0;
    }

    @PostMapping("unfollow/{topicId}")
    @AuthAnnotation
    public boolean unfollowInfinityTopic(@PathVariable long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityTopicRepository.existsByDeletedAndId(false, topicId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        if (infinityTopicRepository.isFollow(topicId, userId) == 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_UNFOLLOWED);
        }
        infinityTopicRepository.followerNumIncrement(topicId, -1);
        return infinityTopicRepository.unFollowTopic(topicId, userId) > 0;
    }

    @GetMapping("list")
    public PageData<InfinityTopic> TopicController() {
        PageRequest pageRequest = PRHelper.page(1, 10);
        return new PageData<>(infinityTopicRepository.findByDeletedFalseOrderByViewNumDesc(pageRequest));
    }
}
