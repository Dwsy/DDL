package link.dwsy.ddl.XO.ES.article;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String name;
}
