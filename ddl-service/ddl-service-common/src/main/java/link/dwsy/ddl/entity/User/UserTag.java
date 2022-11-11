package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.QA.QaGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Entity
@Table(name = "user_tag")
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "createTime", "deleted", "lastModifiedTime"})
public class UserTag extends BaseEntity {


    private String name;

    @Builder.Default
    private int useNum = 0;

    //排序权重
    private Integer weight;

    //是否在主页显示
    @Builder.Default
    private boolean indexPageDisplay = false;

    private String tagInfo;

    //
    @ManyToMany(mappedBy = "userTags"
            , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

//    @ManyToMany(mappedBy = "userTags",
//            fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<UserInfo> userInfos;

    @ManyToOne
    private QaGroup qaGroup;

    public UserTag() {

    }
}
