package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/12/19
 */
@Data
@AllArgsConstructor
public class HeatmapData {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date date;

    int count;

}
