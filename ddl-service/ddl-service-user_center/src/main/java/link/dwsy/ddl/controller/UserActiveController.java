package link.dwsy.ddl.controller;

import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */
@RestController
@RequestMapping("/active")
public class UserActive {

    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    UserSupport userSupport;

    @PostMapping("/check")
    public String checkIn(){
        LoginUserInfo currentUser = userSupport.getCurrentUser();

        return "check in";
    }
}
