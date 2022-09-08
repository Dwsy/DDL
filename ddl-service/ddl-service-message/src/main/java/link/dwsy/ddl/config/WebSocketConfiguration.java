package link.dwsy.ddl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author Dwsy
 * @Date 2022/9/3
 */

@Configuration
//@EnableWebSocket
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

        //Redis消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        //设置Redis链接工厂
        container.setConnectionFactory(connectionFactory);

        return container;
    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new TextWebSocketHandler(), "/ws")
//                .addInterceptors(new HttpSessionHandshakeInterceptor());
//    }
}


