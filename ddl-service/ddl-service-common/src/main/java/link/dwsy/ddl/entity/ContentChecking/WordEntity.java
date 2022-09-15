package link.dwsy.ddl.entity.ContentChecking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.WordType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */
@Entity
@Table(name = "word")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted","handler", "hibernateLazyInitializer","createTime","lastModifiedTime"})
public class WordEntity extends BaseEntity {

    /**
     * 单词
     */
    private String word;

    /**
     * 类型 1-禁止 2-放行
     */
    private WordType type;

}
