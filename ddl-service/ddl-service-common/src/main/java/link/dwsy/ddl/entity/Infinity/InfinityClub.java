package link.dwsy.ddl.entity.Infinity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@Entity
@Table(name = "infinity_club")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer", "lastModifiedTime"})
public class InfinityClub extends BaseEntity {

    @OneToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User createUser;

    private String name;

    private String description;

    private String cover;

    private String notice;

    private long viewNum;

    private long infinityNum;

    private long followerNum;
}
