package link.dwsy.ddl.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/23
 */
//jpa test

@Entity
@Table(name = "users")
// todo user是关键字要用得加双引号但是jpa并不会。。
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    private String username;

    private String password;

    private String email;

    private String phone;

    private String area;

    @OneToOne(cascade = {CascadeType.ALL})
    private UserInfo userInfo;

    private int level = 0;

    @OneToMany(mappedBy = "user")
    private List<ArticleContent> articleContentList;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", area='" + area + '\'' +
                ", userInfo=" + userInfo +
                ", level=" + level +
                '}';
    }


}
