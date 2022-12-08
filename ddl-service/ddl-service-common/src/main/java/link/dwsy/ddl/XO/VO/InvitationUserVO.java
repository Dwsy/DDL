package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Dwsy
 * @Date 2022/11/10
 */
@Getter
@Setter
@Builder
public class InvitationUserVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String userNickName;

    private String avatar;

    private boolean Invited;


}
