package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Gender;
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
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","createTime","articleContent","lastModifiedTime","user"})
public class UserInfo extends BaseEntity {


    @OneToOne(mappedBy = "userInfo",fetch = FetchType.LAZY)
    private User user;

    @Builder.Default
    private String avatar = "default";

    private String sign;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private Date birth;




    @Transient
    private int level;

}
