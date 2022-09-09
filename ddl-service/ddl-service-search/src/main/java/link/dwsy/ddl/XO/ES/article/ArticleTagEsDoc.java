package link.dwsy.ddl.XO.ES.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTagEsDoc {
    private Long id;
    private String name;
}
