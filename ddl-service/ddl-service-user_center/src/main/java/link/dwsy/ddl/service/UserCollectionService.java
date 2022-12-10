package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.RB.UserCollectionRB;
import link.dwsy.ddl.entity.User.UserCollection;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

public interface UserCollectionService {
    String getCollectionToLink(@PathVariable long id);

    @NotNull String addCollectionToGroup(UserCollectionRB userCollectionRB, CollectionType collectionType, Long gid, Long sid, Long uid);

    String cancelCollection(UserCollectionRB userCollectionRB);

    PageData<UserCollection> getCollectionListByGroupId(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "", name = "collectionType") CollectionType collectionType,
            @PathVariable long groupId);
}
