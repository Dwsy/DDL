package link.dwsy.ddl.Service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */

public interface FileService {
    String uploadPictureFile(MultipartFile file);

    String[] allowedFilePostfix = new String[] { "png", "bmp", "jpg", "jpeg","pdf","webp" };

    default boolean isFileAllowedPostfix(String fileName) {
        for (String ext : allowedFilePostfix) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
