package link.dwsy.ddl.demo.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/10/12
 */

@ConfigurationProperties(prefix = "snowflake")
@Getter
@SuppressWarnings("all")
public class JpaAuditorAware implements AuditorAware<Long> {

    private int workerId;

    private int dataCenterId;

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }


    public void setDataCenterId(int dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    @Override
    public  Optional<Long> getCurrentAuditor() {
        Snowflake snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
        Long id = snowflake.nextId();
        return Optional.of(id);
    }
}
