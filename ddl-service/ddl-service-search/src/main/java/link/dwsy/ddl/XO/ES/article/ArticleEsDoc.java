package link.dwsy.ddl.XO.ES.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEsDoc {
    private Long id;
    private String userId;
    private String userNickName;
    private String title;
    private String content;
    private String summary;
    private String group;
    private List<ArticleTagEsDoc> tagList;
    private int viewNum;
    private int collectNum;
    private int upNum;
    private int downNum;
    private List<String> suggestion;
}

