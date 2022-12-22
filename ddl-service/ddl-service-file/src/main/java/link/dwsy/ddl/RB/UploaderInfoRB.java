package link.dwsy.ddl.RB;

import link.dwsy.ddl.Enum.FileStorage;
import link.dwsy.ddl.Enum.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploaderInfoRB {
    private Long userId;

    private String fileName;

    private FileType fileType;

    private String fileUrl;


    private FileStorage fileStorage;

    private String fileMd5;

    private boolean ban;
}
