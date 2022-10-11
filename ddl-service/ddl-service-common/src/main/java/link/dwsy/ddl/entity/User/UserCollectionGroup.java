package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
@Entity
@Table(name = "user_collection_group", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"user_id", "group_name"})})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime"})
public class UserCollectionGroup extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "group_name")
    private String groupName;

    private int collectionNum;

    private int groupOrder;

    private String introduction;

}
