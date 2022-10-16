package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */
@Entity
@Table(name = "user_following")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime","articleFields"})
public class UserFollowing extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long followingUserId;

    @Transient
    private boolean mutual;

}
