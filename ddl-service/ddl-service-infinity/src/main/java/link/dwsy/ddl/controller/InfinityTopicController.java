package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.InfinityTopicRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
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
        return infinityTopicRepository.save(infinityTopic);
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
        return infinityTopicRepository.save(infinityTopic);
    }

    @DeleteMapping("{topicId}")
    @AuthAnnotation
    public void deleteInfinityTopic(@PathVariable Long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        InfinityTopic infinityTopic = infinityTopicRepository.findById(topicId)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
        if (infinityTopic.getCreateUser().getId() != userId) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        infinityTopicRepository.delete(infinityTopic);
    }

    @PostMapping("follow/{topicId}")
    @AuthAnnotation
    public boolean followInfinityTopic(@PathVariable long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityTopicRepository.existsByDeletedAndId(false, topicId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        if (infinityTopicRepository.isFollow(topicId,userId) > 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_FOLLOWED);
        }
        return infinityTopicRepository.followTopic( topicId,userId) > 0;
    }

    @PostMapping("unfollow/{topicId}")
    @AuthAnnotation
    public boolean unfollowInfinityTopic(@PathVariable long topicId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityTopicRepository.existsByDeletedAndId(false, topicId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
        }
        if (infinityTopicRepository.isFollow(topicId,userId) == 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_UNFOLLOWED);
        }
        return infinityTopicRepository.unFollowTopic(topicId,userId) > 0;
    }
}
