package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.repository.User.UserPointsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserPointsProcess {
    @Resource
    UserPointsRepository userPointsRepository;


    public boolean pointsLimit(UserPointsMessage message) {
        PointsType pointsType = message.getPointsType();
        Long userId = message.getUserId();

        return true;
    }
}
