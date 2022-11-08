package link.dwsy.ddl.XO.ES.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/11/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEsDoc {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String userNickName;

    private String avatar;
}
