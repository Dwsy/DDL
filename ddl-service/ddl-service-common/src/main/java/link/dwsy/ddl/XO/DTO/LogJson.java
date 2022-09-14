package link.dwsy.ddl.XO.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author Dwsy
 * @Date 2022/9/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogJson {
    private String ip;
    private String url;
    private String method;
    private String classMethod;
    private Map<String,Object> requestParams;
}
