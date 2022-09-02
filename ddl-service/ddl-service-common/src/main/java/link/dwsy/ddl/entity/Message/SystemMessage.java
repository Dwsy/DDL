package link.dwsy.ddl.entity.Message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@Entity
@Table(name = "system_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
public class SystemMessage extends BaseEntity {
    private long formSystemUserId;

    private long toUserId;

    private String conversationId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

}
