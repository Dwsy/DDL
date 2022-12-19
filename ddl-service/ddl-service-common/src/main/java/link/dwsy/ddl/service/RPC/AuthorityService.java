package link.dwsy.ddl.service.RPC;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dwsy
 * @Date 2022/12/18
 */
@FeignClient(name = "ddl-authority-center")
public interface AuthorityService {
    @PostMapping("/authority/active")
    boolean active(HttpServletRequest request);
}
