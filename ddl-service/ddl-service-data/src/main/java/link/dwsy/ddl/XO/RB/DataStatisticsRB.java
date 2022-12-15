package link.dwsy.ddl.XO.RB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author Dwsy
 * @Date 2022/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataStatisticsRB {


   private LocalDate startDate;

    private LocalDate endDate;
}
