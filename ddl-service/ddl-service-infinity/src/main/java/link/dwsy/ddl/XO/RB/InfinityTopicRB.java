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


    private String description;

    private String cover;

    private String notice;
}
