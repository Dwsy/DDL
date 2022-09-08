package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
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
public class UserActiveController {

    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    UserSupport userSupport;

    @Resource
    UserActiveServiceImpl userActiveService;

    @PostMapping("/check")
    public String checkIn(){
        if (userActiveService.ActiveLog(UserActiveType.Check_In,null)) {
            return "签到成功";
        } else {
            return "今日已签到";
        }
    }
}
