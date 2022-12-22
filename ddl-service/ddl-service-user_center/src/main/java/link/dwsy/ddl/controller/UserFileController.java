package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.UploaderInfoRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.IgnoreResponseAdvice;
import link.dwsy.ddl.entity.User.UserFileRef;
import link.dwsy.ddl.repository.User.UserFileRefRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */
@RestController
@RequestMapping("file")
@Slf4j
public class UserFileController {

    @Resource
    private UserSupport userSupport;
    @Resource
    private UserFileRefRepository userFileRefRepostitory;

    @PostMapping("upload/save/userInfo")
    @AuthAnnotation
    @IgnoreResponseAdvice
    public void upload(@RequestBody UploaderInfoRB uploaderInfo) {
        Long userId = userSupport.getCurrentUser().getId();
        UserFileRef userFileRef = UserFileRef.builder()
                .fileMd5(uploaderInfo.getFileMd5())
                .fileName(uploaderInfo.getFileName())
                .fileStorage(uploaderInfo.getFileStorage())
                .fileType(uploaderInfo.getFileType())
                .fileUrl(uploaderInfo.getFileUrl())
                .userId(userId)
                .build();
        userFileRefRepostitory.save(userFileRef);
        log.info("userId:{}", uploaderInfo);
        log.info("上传文件");
    }
}
