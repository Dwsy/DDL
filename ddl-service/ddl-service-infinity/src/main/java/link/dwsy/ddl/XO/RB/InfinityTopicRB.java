package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@Data
public class InfinityTopicRB {
    private Long id;

    @Size(min = 1, max = 100, message = "话题名称长度不合法")
    private String name;

    @Size(max = 255, message = "话题描述长度不合法")
    private String description;

    @Size(max = 255, message = "话题封面长度不合法")
    private String cover;

    @Size(max = 255, message = "话题公告长度不合法")
    private String notice;
}
