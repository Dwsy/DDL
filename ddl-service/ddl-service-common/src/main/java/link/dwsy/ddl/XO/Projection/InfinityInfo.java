package link.dwsy.ddl.XO.Projection;

import java.util.List;

/**
 * A Projection for the {@link link.dwsy.ddl.entity.Infinity.Infinity} entity
 */
public interface InfinityInfo {
    List<InfinityTopicInfo> getInfinityTopics();

    InfinityClubInfo getInfinityClub();

    /**
     * A Projection for the {@link link.dwsy.ddl.entity.Infinity.InfinityTopic} entity
     */
    interface InfinityTopicInfo {
        long getId();
    }

    /**
     * A Projection for the {@link link.dwsy.ddl.entity.Infinity.InfinityClub} entity
     */
    interface InfinityClubInfo {
        long getId();
    }
}