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
 * @Date 2022/9/1
 */

@Entity
@Table(name = "user_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})

public class UserMessage extends BaseEntity {

    private long formUserId;

    private long toUserId;

    private String conversationId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Builder.Default
    private MessageState status=MessageState.UNREAD;

    @Override
    public String toString() {
        return "UserMessage{" +
                "formUserId=" + formUserId +
                ", toUserId=" + toUserId +
                ", time='" + super.getCreateTime() + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
