package link.dwsy.ddl.entity.Infinity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@Entity
@Table(name = "infinity_topic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer", "lastModifiedTime"})
public class InfinityTopic extends BaseEntity {
    @ManyToMany(mappedBy = "infinityTopics",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Infinity> infinity;

    @OneToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User createUser;

    private String name;

    private String description;

    private String notice;


    private String cover;

    private long viewNum;

    private long infinityNum;

    private long followerNum;

}
