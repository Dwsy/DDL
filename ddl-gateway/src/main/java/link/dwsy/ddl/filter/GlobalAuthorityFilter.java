package link.dwsy.ddl.filter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.config.TreadPoolConfig;
import link.dwsy.ddl.constant.GatewayConstant;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.JwtToken;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.core.domain.UsernameAndPassword;
import link.dwsy.ddl.core.utils.TokenParseUtil;
import link.dwsy.ddl.core.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * <h1>全局鉴权过滤器</h1>
 */
@Slf4j
@Component
public class GlobalAuthorityFilter implements GlobalFilter, Ordered {

    /**
     * 注册中心客户端, 可以从注册中心中获取服务实例信息
     */
    private final LoadBalancerClient loadBalancerClient;
    private final RestTemplate restTemplate;

    private final RedisTemplate<String, String> redisTemplate;

    private final TreadPoolConfig treadPool;

    public GlobalAuthorityFilter(LoadBalancerClient loadBalancerClient,
                                 RestTemplate restTemplate, RedisTemplate<String, String> redisTemplate, TreadPoolConfig treadPoolConfig) {
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.treadPool = treadPoolConfig;
    }

    /**
     * <h2>登录、注册、鉴权</h2>
     * 1. 如果是登录或注册, 则去授权中心拿到 Token 并返回给客户端
     * 2. 如果是访问其他的服务,如果有token并且鉴权通过后将JWT令牌中的用户信息解析出来，然后存入请求的Header中，
     * 这样后续服务就不需要解析JWT令牌了，可以直接从请求的Header中获取到用户信息
     * {@link LoginUserInfo}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String s = headers.getFirst("loginUserInfo");
        String path = request.getURI().getPath();
        log.info(path);
        if (s == null || s.isEmpty()) {
            // 1. 登录
            if (path.contains(GatewayConstant.LOGIN_URI)) {
                // 去授权中心拿 token
                String token = getTokenFromAuthorityCenter(
                        request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URL_FORMAT, true
                );
                if (token == null) {
                    response.setStatusCode(HttpStatus.OK);
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    DataBuffer wrap = bufferFactory.wrap(JSON.toJSONString(R.fail("user name or password is incorrect")).getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(wrap));
                }
                // header 中不能设置 null
                response.getHeaders().add(
                        HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        HttpHeaders.AUTHORIZATION
                );
                response.getHeaders().add(
                        HttpHeaders.AUTHORIZATION,
                        token
                );
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            // 2. 注册
            if (path.contains(GatewayConstant.REGISTER_URI)) {
                // 去授权中心拿 token: 先创建用户, 再返回 Token
                String token = getTokenFromAuthorityCenter(
                        request,
                        GatewayConstant.AUTHORITY_CENTER_REGISTER_URL_FORMAT, true
                );
                if (token == null) {
                    response.setStatusCode(HttpStatus.OK);
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    DataBuffer wrap = bufferFactory.wrap(JSON.toJSONString(R.fail("The user name already exists")).getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(wrap));
                }
                response.getHeaders().add(
                        HttpHeaders.AUTHORIZATION,
                        token
                );
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            // 3. 退出
            if (path.contains(GatewayConstant.LOGOUT_URI)) {
                // token redis blacklist
                String token = headers.getFirst(TokenConstants.AUTHENTICATION);
                if (token == null) {
                    return null;
                }
                try {
                    TokenParseUtil.parseUserInfoFromToken(token);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String blackToken = redisTemplate.opsForValue().get(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + token);
                if (!StrUtil.isBlank(blackToken)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
                redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + TokenUtil.getTokenDigest(token), "1",
                        365, TimeUnit.DAYS);
                response.setStatusCode(HttpStatus.OK);
                DataBufferFactory bufferFactory = response.bufferFactory();
                DataBuffer wrap;

                wrap = bufferFactory.wrap(JSON.toJSONString(R.ok("logout success")).getBytes(StandardCharsets.UTF_8));

                DataBuffer finalWrap = wrap;


                return response.writeWith(Flux.just(finalWrap));
            }

            // 3. Github OAuth
            if (path.contains(GatewayConstant.GITHUB_OAUTH)) {
                // 去授权中心拿 token
                String token = getTokenFromAuthorityCenter(
                        request, GatewayConstant.AUTHORITY_GITHUB_OAUTH_URL_FORMAT, false
                );
                // header 中不能设置 null
                response.getHeaders().add(
                        HttpHeaders.AUTHORIZATION,
                        null == token ? "null" : token
                );
                String redirectUrl = "http://localhost:3000/user/login" + "?token=" + token;
                response.getHeaders().set(HttpHeaders.LOCATION, redirectUrl);
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.setComplete();

            }
            // reToken
            if (path.contains(GatewayConstant.GET_USER_INFO)) {
                String token = headers.getFirst(TokenConstants.AUTHENTICATION);
                if (token != null) {
                    Boolean reToken = redisTemplate.opsForValue()
                            .setIfAbsent(Constants.RE_ACTIVE_TOKEN_KEY + TokenUtil.getTokenDigest(token), "1", 8, TimeUnit.HOURS);
                    if (Boolean.TRUE.equals(reToken)) {
                        reActiveToken(token);
                    }
                }
            }
        }

        // 3. 访问其他的服务, 则鉴权, 校验是否能够从 Token 中解析出用户信息后 存入header中

        String token = headers.getFirst(TokenConstants.AUTHENTICATION);
        String back = redisTemplate.opsForValue().get(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + token);
        if (!StrUtil.isBlank(back)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        LoginUserInfo loginUserInfo;
        if (token != null) {
            try {
                loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
                String loginUserInfoJSON = new ObjectMapper().writeValueAsString(loginUserInfo);
                Consumer<HttpHeaders> httpHeaders = httpHeader -> httpHeader.set(Constants.JWT_USER_INFO_KEY, loginUserInfoJSON);
                ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
                ServerWebExchange serverWebExchange = exchange.mutate().request(serverHttpRequest).build();
                return chain.filter(serverWebExchange);
            } catch (Exception ex) {
                log.error("parse user info from token error: [{}]", ex.getMessage(), ex);
            }
        } else if (headers.getFirst(Constants.JWT_USER_INFO_KEY) != null) {
            Consumer<HttpHeaders> httpHeaders = httpHeader -> httpHeader.set(Constants.JWT_USER_INFO_KEY, "null");
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
            ServerWebExchange serverWebExchange = exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(serverWebExchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }

    /**
     * <h2>从授权中心获取 Token</h2>
     */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat, boolean isPostMethod) {


        // 升级springboot2.7后使用WebFlux异步调用，同步会报错
        Future<ServiceInstance> future = treadPool.buildHttpApiThreadPool().submit(() -> loadBalancerClient.choose(Constants.AUTHORITY_CENTER_SERVICE_ID));
        ServiceInstance serviceInstance = null;
        try {
            serviceInstance = future.get();
        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
            return null;
//            todo 优化
        } catch (InterruptedException e) {
            return null;
//            todo 优化
        }
//        executorService.shutdown();


        log.info("Nacos Client Info: [{}], [{}], [{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(),
                JSON.toJSONString(serviceInstance.getMetadata()));

        String requestUrl = String.format(
                uriFormat, serviceInstance.getHost(), serviceInstance.getPort()
        );
        UsernameAndPassword requestBody = JSON.parseObject(
                parseBodyFromRequest(request), UsernameAndPassword.class
        );
        log.info("login request url and body: [{}], [{}]", requestUrl,
                JSON.toJSONString(requestBody));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JwtToken token = null;
        if (isPostMethod) {
            token = restTemplate.postForObject(
                    requestUrl,
                    new HttpEntity<>(JSON.toJSONString(requestBody), headers),
                    JwtToken.class
            );
        } else {
            String code = request.getQueryParams().getFirst("code");
            log.info("code{}", code);
            token = restTemplate.getForObject(
                    requestUrl, JwtToken.class, code
            );;
        }


        if (null != token) {
            return token.getToken();
        }

        return null;
    }

    /**
     * <h2>从 Post 请求中获取到请求数据</h2>
     */
    private String parseBodyFromRequest(ServerHttpRequest request) {

        // 获取请求体
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();

        // 订阅缓冲区去消费请求体中的数据
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            // 一定要使用 DataBufferUtils.release 释放掉, 否则, 会出现内存泄露
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });

        // 获取 request body
        return bodyRef.get();
    }


    private void reActiveToken(String token) {


        // 升级springboot2.7后使用WebFlux异步调用，同步会报错
        Future<ServiceInstance> future = treadPool.buildHttpApiThreadPool().submit(() -> loadBalancerClient.choose(Constants.AUTHORITY_CENTER_SERVICE_ID));
        ServiceInstance serviceInstance = null;
        try {
            serviceInstance = future.get();
        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
            return;
//            todo 优化
        } catch (InterruptedException e) {
            return;
//            todo 优化
        }
        String requestUrl = String.format(
                GatewayConstant.AUTHORITY_ACTIVE_URL_FORMAT, serviceInstance.getHost(), serviceInstance.getPort()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(TokenConstants.AUTHENTICATION, token);

        restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(null, headers), Boolean.class
        );
    }

}
