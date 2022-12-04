package link.dwsy.ddl.config;


import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {
    @Bean(value = "dockerBean")
    public Docket dockerBean() {
        //指定使用Swagger2规范
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        //描述字段支持Markdown语法
                        .description("# Knife4j RESTful APIs")
                        .termsOfServiceUrl("https://doc.xiaominfo.com/")
                        .contact(new Contact("name","url","email"))
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("搜索服务")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("link.dwsy.ddl"))
                .paths(PathSelectors.any())
                .build();
    }
    @Bean
    public WebMvcEndpointHandlerMapping webMvcEndpointHandlerMapping(WebEndpointsSupplier wes
            , ServletEndpointsSupplier ses, ControllerEndpointsSupplier ces, EndpointMediaTypes emt
            , CorsEndpointProperties cep, WebEndpointProperties wep, Environment env) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = wes.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(ses.getEndpoints());
        allEndpoints.addAll(ces.getEndpoints());
        String basePath = wep.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = shouldRegisterLinksMapping(wep, env, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, emt
                , cep.toCorsConfiguration(), new EndpointLinksResolver(
                allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }
    /**
     * shouldRegisterLinksMapping
     *
     * @param wep
     * @param env
     * @param basePath
     * @return
     */
    private boolean shouldRegisterLinksMapping(WebEndpointProperties wep, Environment env, String basePath) {
        return wep.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(env).equals(ManagementPortType.DIFFERENT));
    }
}