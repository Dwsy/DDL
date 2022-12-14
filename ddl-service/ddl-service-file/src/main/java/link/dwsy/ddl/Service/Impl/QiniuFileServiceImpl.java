package link.dwsy.ddl.Service.Impl;

import com.alibaba.fastjson2.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import link.dwsy.ddl.Service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class QiniuFileServiceImpl implements FileService {

    @Value("${qiniu.accessKey}")
    private String accessKey;      //公钥
    @Value("${qiniu.secretKey}")
    private String accessSecretKey;   //私钥
    @Value("${qiniu.bucketName}")
    private String bucketName;   // 存储空间
    @Value("${qiniu.path}")
    private String path;       // 域名
    @Value("${qiniu.documentName}")
    private String documentName;   // 空间里的文件夹


    /**
     * @param file 前端传来的图片
     * @return 图片的访问路径
     */
    public String uploadPictureFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return null;
        }
        // 生成文件名
        String fileName = getRandomImgName(filename);
        int dotPos = filename.lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = filename.substring(dotPos + 1).toLowerCase();
        // 判断是否是合法的文件后缀
        if (!isFileAllowedPostfix(fileExt)) {
            return null;
        }
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.regionCnEast2());  //根据自己的对象空间的地址选（华东）
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        try {
            byte[] uploadBytes = file.getBytes();
            Auth auth = Auth.create(accessKey, accessSecretKey);
            String upToken = auth.uploadToken(bucketName);
            Response response = uploadManager.put(uploadBytes, documentName + "/" + fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return path + "/" + documentName + "/" + fileName;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 生成唯一图片名称
     * @Param: fileName
     * @return: 云服务器fileName
     */
    public  String getRandomImgName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(".");

        if (fileName.isEmpty() || index == -1) {
            throw new IllegalArgumentException();
        }
        // 获取文件后缀
        String suffix = fileName.substring(index).toLowerCase();
        // 生成UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 拼接新的名称
        return uuid + suffix;
    }

}