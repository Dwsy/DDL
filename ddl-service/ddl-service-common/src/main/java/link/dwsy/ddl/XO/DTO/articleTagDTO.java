package link.dwsy.ddl.XO.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Data
@Builder
@AllArgsConstructor
public class articleTagDTO {

    private Long id;

    private String name;

    private Long articleNum;

    private String tagInfo;

//    public static articleTagDTO convert(ArticleTag item) {
//        if (item == null) {
//            return null;
//        }
//        articleTagDTO result = new articleTagDTO();
//        result.setId(item.getId());
//        result.setName(item.getName());
//        result.setArticleNum(item.getArticleNum());
//        result.setTagInfo(item.getTagInfo());
//        return result;
//    }


}
