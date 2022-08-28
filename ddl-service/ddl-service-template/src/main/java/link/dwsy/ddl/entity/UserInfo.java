package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_info")
// todo user是关键字要用得加双引号但是jpa并不会。。
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","createTime","articleContent","lastModifiedTime","user"})
public class UserInfo extends BaseEntity {


    @OneToOne(mappedBy = "userInfo",fetch = FetchType.LAZY)
    private User user;

    private String avatar = "default";

    private String sign;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birth;

}
