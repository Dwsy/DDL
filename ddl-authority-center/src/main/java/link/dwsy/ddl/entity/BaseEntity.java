package link.dwsy.ddl.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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

    private boolean deleted = false;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date lastModifiedTime;
}