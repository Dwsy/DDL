package link.dwsy.ddl.entity;

import link.dwsy.ddl.XO.Enum.Gender;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_info")
// todo user是关键字要用得加双引号但是jpa并不会。。
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserInfo extends BaseEntity {

    private String avatar = "default";


    private String sign;


    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birth;

}
