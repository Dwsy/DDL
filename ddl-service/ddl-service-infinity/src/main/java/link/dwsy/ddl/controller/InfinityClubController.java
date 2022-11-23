package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.InfinityClubRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
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
@RequestMapping("club")
@Slf4j
public class InfinityClubController {

    @Resource
    private UserSupport userSupport;

    @Resource
    private InfinityRepository infinityRepository;

    @Resource
    private InfinityClubRepository infinityClubRepository;


    @PostMapping
    @AuthAnnotation
    public InfinityClub createInfinityClub(@Validated @RequestBody InfinityClubRB infinityClubRB) {
        Long userId = userSupport.getCurrentUser().getId();
        if (infinityClubRepository.existsByDeletedFalseAndName(infinityClubRB.getName())) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST);
        }
        InfinityClub infinityClub = new InfinityClub();
        infinityClub.setCreateUser((User) new User().setId(userId));
        infinityClub.setName(infinityClubRB.getName());
        infinityClub.setCover(infinityClubRB.getCover());
        infinityClub.setDescription(infinityClubRB.getDescription());
        infinityClub.setNotice(infinityClubRB.getNotice());
        return infinityClubRepository.save(infinityClub);
    }

    @PutMapping
    @AuthAnnotation
    public InfinityClub updateInfinityClub(@Validated @RequestBody InfinityClubRB infinityClubRB) {
        Long userId = userSupport.getCurrentUser().getId();
        Long ClubId = infinityClubRB.getId();
        if (ClubId == null) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        InfinityClub infinityClub = infinityClubRepository.findById(ClubId)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
        if (infinityClub.getCreateUser().getId() != userId) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST);
        }
        infinityClub.setName(infinityClubRB.getName());
        infinityClub.setCover(infinityClubRB.getCover());
        infinityClub.setDescription(infinityClubRB.getDescription());
        infinityClub.setNotice(infinityClubRB.getNotice());
        return infinityClubRepository.save(infinityClub);
    }

    @DeleteMapping("{clubId}")
    @AuthAnnotation
    public void deleteInfinityClub(@PathVariable Long clubId) {
        Long userId = userSupport.getCurrentUser().getId();
        InfinityClub infinityClub = infinityClubRepository.findById(clubId)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
        if (infinityClub.getCreateUser().getId() != userId) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST);
        }
        infinityClubRepository.delete(infinityClub);
    }


    @PostMapping("follow/{clubId}")
    @AuthAnnotation
    public boolean followInfinityTopic(@PathVariable long clubId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityClubRepository.existsByDeletedAndId(false, clubId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST);
        }
        if (infinityClubRepository.isFollow(clubId, userId) > 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_FOLLOWED);
        }
        return infinityClubRepository.followClub(clubId, userId) > 0;
    }

    @PostMapping("unfollow/{clubId}")
    @AuthAnnotation
    public boolean unfollowInfinityTopic(@PathVariable long clubId) {
        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityClubRepository.existsByDeletedAndId(false, clubId)) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST);
        }
        if (infinityClubRepository.isFollow(clubId, userId) == 0) {
            throw new CodeException(CustomerErrorCode.INFINITY_CLUB_UNFOLLOWED);
        }
        return infinityClubRepository.unFollowClub(clubId, userId) > 0;
    }
}
