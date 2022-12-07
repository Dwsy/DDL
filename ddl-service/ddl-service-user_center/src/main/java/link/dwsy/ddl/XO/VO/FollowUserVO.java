package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.entity.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowUserVO extends User {

    private boolean mutual;

    public FollowUserVO(User user) {
        super(user.getUsername(), user.getNickname(), user.getPassword(),
                user.getSalt(), user.getEmail(), user.getPhone(), user.getArea(), user.getExperience(), null,
                user.getUserInfo(), user.getLevel(), user.getArticleFields(), user.getChannels(), user.getFollowing());
    }
}
