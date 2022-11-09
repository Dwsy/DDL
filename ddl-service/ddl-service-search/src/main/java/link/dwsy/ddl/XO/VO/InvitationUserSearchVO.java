package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.XO.ES.User.UserEsDoc;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Dwsy
 * @Date 2022/11/10
 */
@Getter
@Setter
public class InvitationUserSearchVO extends UserEsDoc {
    public InvitationUserSearchVO(Long userId, String userNickName, String avatar, boolean invited) {
        super(userId, userNickName, avatar);
        Invited = invited;
    }



    private boolean Invited;


}
