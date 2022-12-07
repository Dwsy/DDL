package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.XO.RB.UserInfoRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserInfo;
import link.dwsy.ddl.entity.User.UserTag;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.repository.User.UserTagRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("info")
public class UserInfoController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private UserFollowingRepository userFollowingRepository;

    @Resource
    private UserTagRepository tagRepository;

    @GetMapping
    @AuthAnnotation()
    public UserInfo getUserInfo() {
        Long id = userSupport.getCurrentUser().getId();
        UserInfo userInfo = userRepository.findUserByIdAndDeletedIsFalse(id).getUserInfo();
        userInfo.setLevel(userRepository.getUserLevelById(id));
        return userInfo;
    }

    @GetMapping("{id}")
//    @AuthAnnotation()
    public User getUserById(@PathVariable Long id) {
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        if (user != null) {
            LoginUserInfo currentUser = userSupport.getCurrentUser();
            //fixme
            if (currentUser != null) {
                Long currentUserId = currentUser.getId();
                boolean following = userFollowingRepository.existsByUserIdAndFollowingUserIdAndDeletedIsFalse(currentUserId, id);
                if (following) {
                    user.setFollowing(true);
                }
            }
            return user;
        } else {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
    }

    @PutMapping
    public boolean updateUserInfo(@RequestBody UserInfoRB userInfo) {
        Long id = userSupport.getCurrentUser().getId();
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        Optional.ofNullable(userInfo.getAvatar()).ifPresent(user.getUserInfo()::setAvatar);
        Optional.ofNullable(userInfo.getSign()).ifPresent(user.getUserInfo()::setSign);
        Optional.ofNullable(userInfo.getBirth()).ifPresent(user.getUserInfo()::setBirth);
        Optional.ofNullable(userInfo.getNickname()).ifPresent(user::setNickname);
        Optional.ofNullable(userInfo.getGender()).ifPresent(user.getUserInfo()::setGender);
        userRepository.save(user);
        return true;
    }

    @PostMapping("tag")
    @AuthAnnotation
    public void updateUserTag(@RequestBody TagIdsRB tagIdsRB) {
        Set<Long> tagIds = tagIdsRB.getTagIds();
        Long userId = userSupport.getCurrentUser().getId();
        User user = userRepository.findUserByIdAndDeletedIsFalse(userId);
        List<UserTag> tagList = tagRepository.findByDeletedFalseAndIdIn(tagIds);
        user.setUserTags(tagList);
        userRepository.save(user);
    }

    @GetMapping("tag/{id}")
    public List<UserTag> getUserTag(@PathVariable long id) {
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        return user.getUserTags();
    }

    @GetMapping("exp")
    public int getUserExp() {
        Long id = userSupport.getCurrentUser().getId();
        return userRepository.getUserExpById(id);
    }
}
