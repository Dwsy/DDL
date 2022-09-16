package link.dwsy.ddl.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/8/23
 */
//jpa test

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"username","handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime","articleFields"})
public class User extends BaseEntity {

    private String username;

    //昵称
    private String nickname;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String salt;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String phone;

    @JsonIgnore
    private String area;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
    private UserInfo userInfo;

    @Builder.Default
    private int level = 0;




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
