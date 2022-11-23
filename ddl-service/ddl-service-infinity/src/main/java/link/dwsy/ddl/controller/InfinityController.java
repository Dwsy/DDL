package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.RB.InfinityRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
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
@RequestMapping("infinity")
@Slf4j
public class InfinityController {

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
    public void sendInfinity(@Validated @RequestBody InfinityRB infinityRB) {
        Long userId = userSupport.getCurrentUser().getId();
        Long infinityClubId = infinityRB.getInfinityClubId();
        Long infinityTopicId = infinityRB.getInfinityTopicId();
        InfinityClub infinityClub;
        InfinityTopic infinityTopic;
        Infinity infinity = new Infinity();
        if (infinityClubId != null) {
            infinityClub = infinityClubRepository.findById(infinityClubId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
            infinity.setInfinityClub(infinityClub);
        }
        if (infinityTopicId != null) {
            infinityTopic = infinityTopicRepository.findById(infinityTopicId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
            infinity.setInfinityTopic(infinityTopic);
        }
        infinity.setUser((User) new User().setId(userId));
        infinity.setContent(infinityRB.getContent());
        InfinityType type = infinityRB.getType();
        if (type == InfinityType.Tweet) {
            infinity.setType(InfinityType.Tweet);
            infinity.setImgUrlByList(infinityRB.getImgUrlList());
        }//todo other
        infinityRepository.save(infinity);

        log.info("infinityRB: {}", infinityRB);
    }

    @PutMapping("{id}")
    @AuthAnnotation
    public void updateInfinity(@Validated @RequestBody InfinityRB infinityRB, @PathVariable long id) {
        Long userId = userSupport.getCurrentUser().getId();
        Long infinityClubId = infinityRB.getInfinityClubId();
        Long infinityTopicId = infinityRB.getInfinityTopicId();
        InfinityClub infinityClub;
        InfinityTopic infinityTopic;
        Infinity infinity = infinityRepository.findByDeletedFalseAndUser_IdAndId(userId, id);
        if (infinity == null) {
            throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
        }
        if (infinityClubId != null) {
            infinityClub = infinityClubRepository.findById(infinityClubId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
            infinity.setInfinityClub(infinityClub);
        }
        if (infinityTopicId != null) {
            infinityTopic = infinityTopicRepository.findById(infinityTopicId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
            infinity.setInfinityTopic(infinityTopic);
        }
        infinity.setImgUrlByList(infinityRB.getImgUrlList());
        infinity.setContent(infinityRB.getContent());
        infinityRepository.save(infinity);
    }

}
