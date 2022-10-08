package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.UserInfoRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserInfo;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("info")
public class UserInfoController {

    @Resource
    UserRepository userRepository;
    @Resource
    UserSupport userSupport;

    @GetMapping
    @AuthAnnotation(Level = 1)
    public UserInfo getUserInfo() {
        Long id = userSupport.getCurrentUser().getId();
        UserInfo userInfo = userRepository.findUserByIdAndDeletedIsFalse(id).getUserInfo();
        userInfo.setLevel(userRepository.getUserLevelById(id));
        return userInfo;
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
}
