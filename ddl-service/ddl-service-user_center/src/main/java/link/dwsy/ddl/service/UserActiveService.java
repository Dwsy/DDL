package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.VO.UserThumbActiveVO;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

public interface UserActiveService {
    String checkIn();

    @Nullable PageData<UserThumbActiveVO> getUserThumbActive(long uid, UserActiveType type, PageRequest pageRequest);
}
