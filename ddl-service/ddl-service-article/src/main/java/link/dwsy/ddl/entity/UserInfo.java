package link.dwsy.ddl.entity;

import link.dwsy.ddl.xo.Enum.Gender;
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
public class UserInfo extends BaseEntity {


    private String avatar = "default";


    private String sign;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birth;

}
