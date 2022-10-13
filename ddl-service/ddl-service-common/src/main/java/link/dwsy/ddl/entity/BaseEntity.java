package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CreatedBy
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    @Builder.Default
    private boolean deleted = false;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date lastModifiedTime;

}