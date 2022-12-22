package link.dwsy.ddl.Service.RPC;

import link.dwsy.ddl.RB.UploaderInfoRB;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */
@FeignClient(name = "ddl-service-user")
public interface SaveUploaderInfoService {

    @PostMapping("file/upload/save/userInfo")
    void upload(@RequestBody UploaderInfoRB uploaderInfo);

}
