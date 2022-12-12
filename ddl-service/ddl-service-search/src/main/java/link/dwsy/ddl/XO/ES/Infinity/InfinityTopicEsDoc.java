package link.dwsy.ddl.XO.ES.Infinity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfinityTopicEsDoc {
    //      "id":5,"name":"test5","infinityNum":2231,"viewNum":3231
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private String name;
    private long infinityNum;
    private long viewNum;
}
