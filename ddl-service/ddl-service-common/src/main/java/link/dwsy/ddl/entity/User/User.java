package link.dwsy.ddl.entity.User;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.Message.Channel;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
@JsonIgnoreProperties(value = {"username", "handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime", "articleFields"})
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

    private int experience;


    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_tag_ref",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_tag_id")})
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<UserTag> userTags;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private UserInfo userInfo;

    @Builder.Default
    private int level = 0;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ArticleField> articleFields;
    @ManyToMany(mappedBy = "user",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Channel> channels;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean following;



    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean Mutually;

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
