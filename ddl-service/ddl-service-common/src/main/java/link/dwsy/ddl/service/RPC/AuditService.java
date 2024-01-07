package link.dwsy.ddl.service.RPC;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Dwsy
 * @Date 2022/12/4
 */

@FeignClient(name = "ddl-service-audit")
public interface AuditService {

    @PostMapping("/check/contains")
    boolean contains(@RequestParam(name = "text") String text);
    //审核文本内容

}
