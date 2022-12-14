package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime", "articleFields"})
public class UserCollection extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private CollectionType collectionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sourceId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String sourceTitle;

    @Transient
    private String link;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_collection_group_id")
    @JsonIgnore
    private UserCollectionGroup userCollectionGroup;

}
