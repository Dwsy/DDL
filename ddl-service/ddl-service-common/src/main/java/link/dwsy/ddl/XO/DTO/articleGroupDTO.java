package link.dwsy.ddl.XO.DTO;

import link.dwsy.ddl.entity.Article.ArticleGroup;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Data
public class articleGroupDTO {
    private String name;

    private String info;

    public static articleGroupDTO convert(ArticleGroup item) {
        if (item == null) {
            return null;
        }
        articleGroupDTO result = new articleGroupDTO();
        result.setName(item.getName());
        result.setInfo(item.getInfo());
        return result;
    }
}
