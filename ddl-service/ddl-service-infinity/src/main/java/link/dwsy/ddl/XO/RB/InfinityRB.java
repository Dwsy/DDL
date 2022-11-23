package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.InfinityType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */

@Data
public class InfinityRB {

    @Size(min = 1, max = 2000)
    private String content;

    @NotNull
    private InfinityType type;

    @NotNull
    private Long refId;

    private Long infinityTopicId;

    private Long infinityClubId;

    @Size(max = 9, message = "图片数量最大为9张")
    private List<String> imgUrlList;

}
