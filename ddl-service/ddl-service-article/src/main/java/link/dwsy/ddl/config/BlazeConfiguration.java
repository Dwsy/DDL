//package link.dwsy.ddl.config;
//
//import com.blazebit.persistence.Criteria;
//import com.blazebit.persistence.CriteriaBuilderFactory;
//import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
//import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
//import com.blazebit.persistence.spring.data.impl.repository.BlazePersistenceRepositoryFactoryBean;
//import com.blazebit.persistence.view.EntityViewManager;
//import com.blazebit.persistence.view.spi.EntityViewConfiguration;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.annotation.Scope;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//
//import javax.annotation.Resource;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceUnit;
//
///**
// * @Author Dwsy
// * @Date 2022/8/25
// */
////@Configuration
////@EnableEntityViews("link.dwsy.ddl.XO.VO")
////@EnableJpaRepositories(
////        basePackages = "link.dwsy.ddl.entity",
////        repositoryFactoryBeanClass = BlazePersistenceRepositoryFactoryBean.class)
//
//@Configuration
//@EntityScan("link.dwsy.ddl.entity")
//@EnableEntityViews("link.dwsy.ddl.XO.VO")
////@EnableJpaRepositories(basePackages = "link.dwsy.ddl.repository")
//@EnableJpaRepositories(basePackages = "link.dwsy.ddl.repository",
//        repositoryFactoryBeanClass = BlazePersistenceRepositoryFactoryBean.class)
//public class BlazeConfiguration {
//
//    @PersistenceUnit
//    private EntityManagerFactory entityManagerFactory;
//
//    @Bean
//    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
//        CriteriaBuilderConfiguration config = Criteria.getDefault();
//        return config.createCriteriaBuilderFactory(this.entityManagerFactory);
//    }
//
//    @Bean
//    public EntityViewManager createEntityViewManager(CriteriaBuilderFactory cbf,
//                                                     EntityViewConfiguration entityViewConfiguration) {
//        return entityViewConfiguration.createEntityViewManager(cbf);
//    }
//
//}
