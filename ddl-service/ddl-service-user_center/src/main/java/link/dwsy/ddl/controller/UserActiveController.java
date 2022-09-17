package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

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
    @AuthAnnotation(Level = 0)
    public String checkIn() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY + 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        if (userActiveRepository.existsByUserIdAndUserActiveTypeAndCreateTimeLessThanEqual
                (userSupport.getCurrentUser().getId(),UserActiveType.Check_In, zero)) {
            return "今日已签到";
        }
        userActiveService.ActiveLog(UserActiveType.Check_In, null);
        return "签到成功";
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        System.out.println(zero);
    }
}
