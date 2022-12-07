//package link.dwsy.ddl.config;
//
//
//import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
//
//import java.net.URI;
//
///**
// * @Author Dwsy
// * @Date 2022/12/6
// */
//
//public class MyWebSocketLoadBalancer implements LoadBalancerClient {
//
//    @Override
//    public URI choose(String routeId, Object request) {
//        // Get client IP address from request
//        String clientIp = ((ServerHttpRequest) request).getRemoteAddress().getAddress().getHostAddress();
//
//        // Get list of service instances for the given route
//        List<ServiceInstance> instances = discoveryClient.getInstances(routeId);
//
//        // Select service instance based on client IP address
//        ServiceInstance instance = instances.stream()
//                .filter(si -> si.getMetadata().get("ip").equals(clientIp))
//                .findFirst()
//                .orElse(null);
//
//        // Return URI of selected service instance
//        return instance != null ? instance.getUri() : null;
//    }
//}
