package link.dwsy.ddl;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
public class userTest {



    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    UserSupport userSupport;

    @Resource
    UserActiveServiceImpl userActiveService;

    @Test
    public void T() {
        checkIn();
        checkIn();
        checkIn();
    }

    public void checkIn(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY+1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        var uid = 4L;
        if (userActiveRepository.existsByUserIdAndUserActiveTypeAndCreateTimeLessThanEqual
                (uid,UserActiveType.Check_In, zero)) {
            System.out.println("今日已签到");
            return;
        }


        userActiveRepository.save(UserActive.builder()
                .userActiveType(UserActiveType.Check_In).userId(uid)
                .sourceId(null).build());
        System.out.println("签到成功");
    }

}
