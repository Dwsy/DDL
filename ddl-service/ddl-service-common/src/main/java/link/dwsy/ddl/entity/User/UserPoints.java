package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/12
 */

@Entity
@Table(name = "user_points")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted", "createTime", "lastModifiedTime"})
public class UserPoints extends BaseEntity {

    int point;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private PointsType pointsType;

}
