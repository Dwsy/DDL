package link.dwsy.ddl.entity.Message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

@Entity
@Table(name = "channel_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer", "createTime", "lastModifiedTime"})

public class ChannelMessage extends BaseEntity {
    private Long channelId;
    private Long userId;
    @Lob
    @Type(type = "text")
    private String message;
    @Builder.Default
    private MessageState state=MessageState.UNREAD;
}
