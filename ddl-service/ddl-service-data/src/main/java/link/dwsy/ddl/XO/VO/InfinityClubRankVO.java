package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/12/10
 */
@Data
public class InfinityClubRankVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private String name;

    private String description;

    private long viewNum;

    private long infinityNum;

    private long followerNum;

    private long scoreCount;




    public InfinityClubRankVO(InfinityClub infinityClub, int scoreCount) {
        this.scoreCount=scoreCount;
        this.id=infinityClub.getId();
        this.name=infinityClub.getName();

        this.description=infinityClub.getDescription();

        this.viewNum=infinityClub.getViewNum();

        this.infinityNum=infinityClub.getInfinityNum();

        this.followerNum=infinityClub.getFollowerNum();
    }
}
