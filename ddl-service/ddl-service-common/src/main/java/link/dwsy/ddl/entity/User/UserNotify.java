package link.dwsy.ddl.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import link.dwsy.ddl.XO.Enum.Message.NotifyState;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author Dwsy
 * @Date 2022/9/12
 */

@Entity
@Table(name = "user_notify")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "deleted"})
public class UserNotify extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long fromUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long toUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long articleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commentId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long questionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long answerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long infinityId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private NotifyType notifyType;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String formContent;//己方

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String toContent;//他方

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyCommentId;//回复后返回的评论id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyAnswerId;//回答后返回的答案id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyInfinityId;//回答后返回的infinityId

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) //返回值
    private NotifyState notifyState = NotifyState.UNREAD;

    @Builder.Default
    private Boolean cancel = false;

//    private boolean read;

    @Transient
    private String formUserAvatar;

    @Transient
    private String formUserNickname;

}
