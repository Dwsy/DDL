//package link.dwsy.ddl.demo.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
//import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
//import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
//import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
//import org.springframework.boot.actuate.endpoint.web.*;
//import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
//import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
//import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.util.StringUtils;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// * @Author Dwsy
// * @Date 2022/8/24
// */
//
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//public class Knife4jConfiguration {
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .useDefaultResponseMessages(false)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("link.dwsy.ddl"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .description("knife4j")
//                .contact(new Contact("Dwsy", "url", "email"))
//                .version("0.1")
//                .title("knife4j在线API接口文档")
//                .build();
//    }
//
//    /**
//     * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题
//     **/
//    @Bean
//    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
//        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
//        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
//        allEndpoints.addAll(webEndpoints);
//        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
//        String basePath = webEndpointProperties.getBasePath();
//        EndpointMapping endpointMapping = new EndpointMapping(basePath);
//        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
//        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
//    }
//    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
//        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
//    }
//}