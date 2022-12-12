package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/12/10
 */
@Data
public class InfinityTopicRankVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private String name;

    private String description;

    private long viewNum;

    private long infinityNum;

    private long followerNum;

    private long scoreCount;




    public InfinityTopicRankVO(InfinityTopic infinityTopic, int scoreCount) {
        this.scoreCount=scoreCount;
        this.id=infinityTopic.getId();
        this.name=infinityTopic.getName();

        this.description=infinityTopic.getDescription();

        this.viewNum=infinityTopic.getViewNum();

        this.infinityNum=infinityTopic.getInfinityNum();

        this.followerNum=infinityTopic.getFollowerNum();
    }
}
