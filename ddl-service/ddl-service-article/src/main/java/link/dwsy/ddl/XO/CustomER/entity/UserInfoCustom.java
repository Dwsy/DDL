package link.dwsy.ddl.XO.CustomER.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Gender;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","deleted","createTime","lastModifiedTime"})
public class UserInfoCustom extends BaseEntityCustom {


    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private UserCustom user;

    private String avatar = "default";
    
    private String sign;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birth;

    @Override
    public String toString() {
        return "UserInfoCustom{" +
                "avatar='" + avatar + '\'' +
                ", sign='" + sign + '\'' +
                ", gender=" + gender +
                ", birth=" + birth +
                '}';
    }
}
