package link.dwsy.ddl.XO.CustomER.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

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
@JsonIgnoreProperties(value = {"deleted","createTime","lastModifiedTime"})
public class UserCustom extends BaseEntityCustom {

    private String username;

//    private String password;

//    private String email;

//    private String phone;

//    private String area;

    @OneToOne(cascade = {CascadeType.ALL})
    private UserInfoCustom userInfo;

    private int level;

//    @OneToMany(mappedBy = "user")
//    private List<ArticleContent> articleContentList;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userInfo=" + userInfo +
                ", level=" + level +
                '}';
    }


}
