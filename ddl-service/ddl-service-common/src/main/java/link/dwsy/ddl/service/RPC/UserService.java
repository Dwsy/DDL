package link.dwsy.ddl.service.RPC;

import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.util.PageData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Dwsy
 * @Date 2022/12/8
 */

@FeignClient(name = "ddl-service-user",contextId = "UserService")
public interface UserService {

    @GetMapping("follow/follower/list/{id}")
    R<PageData<User>> getUserFollowerByUserId(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size,
            @PathVariable(name = "id") long id);

    @GetMapping("follow/following/list/{id}")
    R<PageData<User>> getUserFollowingByUserId(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size,
            @PathVariable(name = "id") long id);

    @GetMapping("follow/follower/list")
    R<PageData<User>> getUserFollower(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size
    );

    @GetMapping("follow/following/list")
    R<PageData<User>> getUserFollowing(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size
    );
}
