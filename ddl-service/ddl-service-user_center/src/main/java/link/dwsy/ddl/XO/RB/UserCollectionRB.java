package link.dwsy.ddl.XO.RB;

import link.dwsy.ddl.XO.Enum.User.CollectionType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */
@Data
public class UserCollectionRB {
    @NotNull(message = "收藏分组不能为空")
    private Long groupId;

    @NotNull(message = "收藏内容不能为空")
    private Long sourceId;

    @NotNull(message = "收藏类型不能为空")
    private CollectionType collectionType;
}
