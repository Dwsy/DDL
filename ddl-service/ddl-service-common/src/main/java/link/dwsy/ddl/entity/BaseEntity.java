package link.dwsy.ddl.entity;

import com.alibaba.fastjson2.JSON;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Builder.Default
    private boolean deleted = false;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date lastModifiedTime;

}