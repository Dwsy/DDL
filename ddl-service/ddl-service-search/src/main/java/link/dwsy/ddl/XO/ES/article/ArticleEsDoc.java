package link.dwsy.ddl.XO.ES.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String userId;

    private String userNickName;

    private String title;

    private String content;

    private String summary;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private String group;

    private List<ArticleTagEsDoc> tagList;

    private int viewNum;

    //todo add
//    private int commentNum;

    private int collectNum;

    private int upNum;

    private int downNum;

    private List<String> suggestion;
}

