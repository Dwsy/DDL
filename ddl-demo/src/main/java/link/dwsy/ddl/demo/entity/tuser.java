//package link.dwsy.ddl.demo.entity;
//
//import com.baomidou.mybatisplus.annotation.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//
///**
// * @Author Dwsy
// * @Date 2022/8/16
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class tuser {
//    @TableId(type = IdType.AUTO)
//    private Long id;
//    private String username;
//    private String password;
//    private String extraInfo;
//    @TableField(fill = FieldFill.INSERT)
//    private Date createTime;
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private Date updateTime;
//    @TableLogic
//    private Integer deleted=0;
//}
