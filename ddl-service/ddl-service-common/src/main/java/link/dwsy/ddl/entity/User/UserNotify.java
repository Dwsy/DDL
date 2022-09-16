package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.NotifyType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/12
 */

@Entity
@Table(name = "user_notify")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime"})
public class UserNotify extends BaseEntity {
    private long fromUserId;

    private long toUserId;

    private long articleId;

    private long commentId;

    private long questionId;

    private long answerId;

    private NotifyType notifyType;

    private String formContent;//己方

    private String toContent;//他方

}
