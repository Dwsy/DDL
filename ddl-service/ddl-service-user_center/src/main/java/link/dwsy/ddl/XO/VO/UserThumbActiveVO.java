package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/10/10
 */
@Data
@Builder
public class UserThumbActiveVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    private Date createTime;

    private User user;

    private String title;

    private String summary;

    private String banner;

    private UserActiveType userActiveType;

//    private Set<ArticleTag> articleTags;

//    @ManyToOne()
//    private ArticleGroup articleGroup;


}
