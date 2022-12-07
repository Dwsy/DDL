package link.dwsy.ddl.XO.Projection;

import java.util.Date;
import java.util.List;

/**
 * A Projection for the {@link link.dwsy.ddl.entity.User.User} entity
 */
public interface UserTagProjection {
    long getId();

    String getNickname();

    List<UserTagInfo> getUserTags();

    /**
     * A Projection for the {@link link.dwsy.ddl.entity.User.UserTag} entity
     */
    interface UserTagInfo {
        long getId();

        Date getCreateTime();


        String getName();

        int getUseNum();

        String getTagInfo();
    }
}