//package link.dwsy.ddl.handler.mybatisPlus;
//
//import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.reflection.MetaObject;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//
///**
// * @Author Dwsy
// * @Date 2022/8/16
// */
//@Slf4j
//@Component // 一定不要忘记把处理器加到IOC容器中！
//public class TimeMetaObjectHandler implements MetaObjectHandler {
//    // 插入时的填充策略
//    @Override
//    public void insertFill(MetaObject metaObject) {
////        log.info("start insert fill.....");
//        this.setFieldValByName("createTime",new Date(),metaObject);
//        this.setFieldValByName("updateTime",new Date(),metaObject);
//    }
//    // 更新时的填充策略
//    @Override
//    public void updateFill(MetaObject metaObject) {
////        log.info("start update fill.....");
//        this.setFieldValByName("updateTime",new Date(),metaObject);
//    }
//}