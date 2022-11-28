package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.OauthType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/11/28
 */
@Entity
@Table(name = "oauth_user_ref")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"username", "handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime", "articleFields"})
public class OauthUserRef extends BaseEntity{
    private long userId;

    private OauthType oauthType;

    private long oauthId;

    private String oauthNodeId;

    private String oauthName;

    private String oauthAvatar;
}
