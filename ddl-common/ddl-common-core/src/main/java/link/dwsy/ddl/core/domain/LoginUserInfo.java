package link.dwsy.ddl.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>登录用户信息</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserInfo {

    /**
     * 用户 id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    private String nickname;

    private int level;
}
