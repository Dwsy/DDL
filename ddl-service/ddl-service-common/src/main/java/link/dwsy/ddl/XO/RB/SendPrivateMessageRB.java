package link.dwsy.ddl.XO.RB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendPrivateMessageRB {

//    @Size(min = 1, max = 20, message = "formUserId error")
//    private long formUserId;

    @Size(min = 1, max = 20, message = "toUserId error")
    private long toUserId;

    @NotBlank(message = "content blank")
    private String content;
}
