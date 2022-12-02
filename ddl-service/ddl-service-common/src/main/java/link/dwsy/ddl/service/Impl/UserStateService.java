package link.dwsy.ddl.service.Impl;

import link.dwsy.ddl.constants.OtherConstants;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserInfo;
import link.dwsy.ddl.repository.User.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/2
 */

@Service
public class UserStateService {

    @Resource
    UserRepository userRepository;

    public void cancellationUserHandel(User user) {
        if (user != null) {
            if (user.getDeleted()) {
                UserInfo userInfo = user.getUserInfo();
                userInfo.setAvatar(OtherConstants.Cancellation_User_Avatar_Url);
                user.setUsername(OtherConstants.Cancellation_User_Name);
                user.setNickname(OtherConstants.Cancellation_User_Name);
            }
        }
    }

    public boolean isCancellationUserHandel(Long userId) {
        if (userId != null) {
            return userRepository.existsByDeletedTrueAndId(userId);
        }else {
            return false;
        }

    }
}
