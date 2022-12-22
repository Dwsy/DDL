package link.dwsy.ddl.Controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import link.dwsy.ddl.Enum.FileStorage;
import link.dwsy.ddl.Enum.FileType;
import link.dwsy.ddl.RB.UploaderInfoRB;
import link.dwsy.ddl.Service.Impl.QiniuFileServiceImpl;
import link.dwsy.ddl.Service.RPC.SaveUploaderInfoService;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;


@RestController
@Slf4j
public class PictureController {

    @Resource
    private QiniuFileServiceImpl qiniuFileService;

    @Resource
    private SaveUploaderInfoService saveUploaderInfoService;

    @Resource
    private UserSupport userSupport;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam("type") int type) throws IOException {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        if (file.isEmpty()) {
            new R<>(1, "上传失败，请选择文件");
        }
        String imagePath = qiniuFileService.uploadPictureFile(file);
        if (!StrUtil.isEmpty(imagePath)) {
            byte[] uploadBytes = file.getBytes();
            FileType fileType = FileType.values()[type];
            UploaderInfoRB uploaderInfoRB = UploaderInfoRB.builder()
                    .fileType(fileType)
                    .fileMd5(SecureUtil.md5(Arrays.toString(uploadBytes)))
                    .userId(currentUser.getId())
                    .fileUrl(imagePath)
                    .fileName(file.getOriginalFilename())
                    .ban(false)
                    .fileStorage(FileStorage.qiniu).build();
            saveUploaderInfoService.upload(uploaderInfoRB);
            return R.ok(imagePath);
        }
        return R.fail("图片上传失败！");
    }


}