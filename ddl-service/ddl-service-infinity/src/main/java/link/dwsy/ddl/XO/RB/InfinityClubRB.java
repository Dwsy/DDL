package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@Data
public class InfinityClubRB {
    private Long id;

    @Size(min = 1, max = 100, message = "圈子名称长度不合法")
    private String name;

    @Size(min = 1, max = 255, message = "圈子描述长度不合法")
    private String description;

    @Size(min = 1, max = 255, message = "圈子封面长度不合法")
    private String cover;

    @Size(min = 1, max = 255, message = "圈子公告长度不合法")
    private String notice;
}
