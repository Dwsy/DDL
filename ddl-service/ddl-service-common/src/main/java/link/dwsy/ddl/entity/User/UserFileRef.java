package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.FileStorage;
import link.dwsy.ddl.XO.Enum.FileType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */
@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted", "lastModifiedTime"})
public class UserFileRef extends BaseEntity {

    private Long userId;

    private String fileName;

    private FileType fileType;

    private String fileUrl;


    private FileStorage fileStorage;

    private String fileMd5;

    private boolean ban;
}
