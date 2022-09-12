package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/4
 */
@Entity
@Table(name = "user_active")
// todo user是关键字要用得加双引号但是jpa并不会。。
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"username","handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime","articleFields"})
public class UserActive extends BaseEntity {
    private Long userId;

    private UserActiveType userActiveType;

    private Long sourceId;

    private String ua;
}

