//package link.dwsy.ddl.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class WebMvcConfig implements WebMvcConfigurer  {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("*")
//                .maxAge(3600)
//                .allowCredentials(true);
//    }
////
////    public static final String[] JIN_TAI = {"classpath:/META-INF/resources/", "classpath:/resources/",
////            "classpath:/static/", "classpath:/public/"};
////    @Override
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////
////        registry.addResourceHandler("/**")
////                .addResourceLocations(
////                        JIN_TAI);
////        registry.addResourceHandler("swagger-ui.html", "doc.html","swagger.html")
////                .addResourceLocations("classpath:/META-INF/resources/");
////    }
////
////    @Bean
////    public HttpMessageConverter<String> responseBody(){
////        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
////    }
////    @Override
////    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
////        converters.add(responseBody());
////    }
//
//}