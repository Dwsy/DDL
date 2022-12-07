package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.entity.User.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/12/8
 */

@Data
@Builder
public class UserAnswerVO {
    private String questionTitle;
    private String id;
    private User user;
    private int upNum;
    private int downNum;
    private String textPrue;

    private Boolean accepted;

    private Date acceptedTime;

    private String questionFieldId;

    private Date createTime;
}
