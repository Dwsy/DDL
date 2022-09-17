package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
@Entity
@Table(name = "user_collection")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","deleted","createTime","lastModifiedTime","articleFields"})
public class UserCollection extends BaseEntity {

    private Long userId;

    private CollectionType collectionType;

    private Long sourceId;

    private String sourceTitle;

    @ManyToOne
    @JoinColumn(name = "user_collection_group_id")
    private UserCollectionGroup userCollectionGroup;

}
