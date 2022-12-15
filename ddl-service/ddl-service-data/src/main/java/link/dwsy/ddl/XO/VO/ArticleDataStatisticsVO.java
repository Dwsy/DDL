package link.dwsy.ddl.XO.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Dwsy
 * @Date 2022/12/15
 */

@Data
@Builder
@AllArgsConstructor
public class ArticleDataStatisticsVO {
    private int serial;
    private long viewNum;
    private long upNum;
    private long downNum;
    private long commentNum;
    private long collectNum;
}
