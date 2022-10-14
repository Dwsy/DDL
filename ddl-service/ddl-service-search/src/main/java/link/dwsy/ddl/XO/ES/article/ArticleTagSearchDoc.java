package link.dwsy.ddl.XO.ES.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/10/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagSearchDoc {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String id;

    private String name;

    private int articleNum;

    private String groupName;

    private boolean indexPageDisplay;

}
