package link.dwsy.ddl.XO.Message;

import link.dwsy.ddl.XO.Enum.InfinityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/12/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfinityMessage {

    InfinityType infinityType;

    long refId;



}
