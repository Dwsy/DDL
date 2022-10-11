package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */


@Entity
@Table(name = "system_users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"username","handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime","articleFields"})
public class SystemUser extends BaseEntity {
    private String username;

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
}
