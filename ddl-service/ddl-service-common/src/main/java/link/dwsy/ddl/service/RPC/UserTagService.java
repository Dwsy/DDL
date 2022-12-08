package link.dwsy.ddl.service.RPC;

import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserTag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/12/8
 */
@FeignClient(name = "ddl-service-user", contextId = "UserTagService")
public interface UserTagService {
    @PostMapping("tag/user")
    R<List<User>> getUserByTagIds(
            @RequestBody TagIdsRB tagIdsRB
    );

    @GetMapping("tag/user/list/{id}")
    R<List<UserTag>> getUserTagByUserId(@PathVariable(name = "id") long id);
}
