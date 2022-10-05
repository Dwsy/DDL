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
        //上传文件，上传到哪呢？图片服务器七牛云
        //把图片发放到距离图片最近的服务器上，降低我们自身服务器的带宽消耗
        String imagePath = qiniuUtils.upload(file);
        if (!imagePath.isEmpty()){
            //上传成功
            return R.ok(imagePath);
        }
        return R.fail("图片上传失败！");
    }


}