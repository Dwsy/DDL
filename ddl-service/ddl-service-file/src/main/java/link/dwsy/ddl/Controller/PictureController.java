package link.dwsy.ddl.Controller;

import link.dwsy.ddl.Utils.QiniuUtils;
import link.dwsy.ddl.core.domain.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
public class PictureController {

    @Resource
    private QiniuUtils qiniuUtils;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file){
        String imagePath = qiniuUtils.upload(file);
        if (!imagePath.isEmpty()){
            return R.ok(imagePath);
        }
        return R.fail("图片上传失败！");
    }


}