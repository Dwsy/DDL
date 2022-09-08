package link.dwsy.ddl.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private long id;

    private String username;

    private String password;

    private String email;

    private String nickname;

    private String phone;

    private String area;

    @OneToOne(cascade = {CascadeType.ALL})
    private UserInfo userInfo;

    @Builder.Default
    private int Level = 0;
//    @CreatedDate
//    private Date create_time;
//    @LastModifiedDate
//    private Date update_time;
//
//    private boolean deleted = false;


}
