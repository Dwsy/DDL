package link.dwsy.ddl.entity.Message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */
@Entity
@Table(name = "channel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted","handler", "hibernateLazyInitializer","createTime","lastModifiedTime"})
public class Channel extends BaseEntity {
    private String name;

    private String description;

    @ManyToMany(
            fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "channel_user_ref",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @JsonIgnore
    private List<User> user;

    private String cover;

    @Builder.Default
    private ChannelState status=ChannelState.NORMAL;

    private boolean isPublic;

    private boolean isReadOnly;
}
