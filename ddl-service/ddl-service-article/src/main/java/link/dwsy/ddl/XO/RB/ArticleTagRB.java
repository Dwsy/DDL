package link.dwsy.ddl.XO.RB;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

@Data
public class ArticleTagRB {
    @NotBlank
    private String name;


    private String tagInfo;
}
