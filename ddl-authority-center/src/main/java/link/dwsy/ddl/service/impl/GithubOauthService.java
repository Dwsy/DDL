package link.dwsy.ddl.service.impl;

import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.DTO.GithubUserInfo;
import link.dwsy.ddl.XO.Enum.OauthType;
import link.dwsy.ddl.constant.AuthorityConstant;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.entity.OauthUserRef;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.entity.UserInfo;
import link.dwsy.ddl.repository.OauthUserRefRepository;
import link.dwsy.ddl.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/11/28
 */

@Service
@Slf4j
public class GithubOauthService {
    private final String accessTokenUrl = "https://github.com/login/oauth/access_token";
    private final String userInfoUrl = "https://api.github.com/user";
    //    private final String accessTokenUrlJsp = "https://github.dwsy.link/login/oauth/access_token/";
//    private final String userInfoUrlJsp = "https://ghapi.dwsy.link/user/";
    @Value("${github.oauth.id}")
    private String clientId;
    @Value("${github.oauth.secret}")
    private String secret;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserRepository userRepository;

    @Resource
    private OauthUserRefRepository oauthUserRefRepository;

    @Resource
    private TokenServiceImpl tokenService;


    public String test() {
        return secret;
    }

    public GithubUserInfo getUserInfo(String code) {


        //发送请求
//        String content = restTemplate.getForObject(, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 构建请求响应实体对象
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        // post请求方式
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl + "?client_id=" + clientId +
                "&client_secret=" + secret +
                "&code=" + code, httpEntity, String.class);
        // 获取请求响应结果
        String result = responseEntity.getBody();
        //
        log.info("远程请求github授权地址，获取access_token:{}", result);

        //解析响应结果
        if (result == null) {
            return null;
        }
        // 获取access_token
        String access_token = result.split("=")[1];

        return getUserInfoByAccessToken(access_token.split("&")[0]);
    }


    private GithubUserInfo getUserInfoByAccessToken(String access_token) {


        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 把access_token放入请求头
        headers.add("Authorization", "token " + access_token);
        // 构建请求响应实体对象
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        // get请求方式
        ResponseEntity<String> responseEntity = restTemplate.exchange(userInfoUrl,
                HttpMethod.GET,
                httpEntity,
                String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            // 获取请求响应结果
            return null;
        }
        // 获取请求响应结果
        String result = responseEntity.getBody();

        log.info(result);
        return JSON.parseObject(result, GithubUserInfo.class);
        // 把json字符串转换为对象
//        OAuthUser oAuthUser = JSON.parseObject(result, OAuthUser.class);
//        return oAuthUser;
    }


    public String generateToken(GithubUserInfo userInfo) throws Exception {
        OauthUserRef oauthUserRef = oauthUserRefRepository.
                findByDeletedFalseAndOauthIdAndOauthNodeIdAndOauthType(userInfo.getId(), userInfo.getNode_id(), OauthType.Github);
        if (oauthUserRef != null) {
            long userId = oauthUserRef.getUserId();
            Optional<User> userOptional = userRepository.findByDeletedFalseAndId(userId);
            if (userOptional.isEmpty()) {
                //todo
                return null;
            }
            User user = userOptional.get();
            return tokenService.getToken(AuthorityConstant.DEFAULT_EXPIRE_DAY, user);
        } else {
            return registerUserAndGenerateToken(userInfo);
        }
    }


    public String registerUserAndGenerateToken(GithubUserInfo userInfo) throws Exception {
        // {front} 明文密码-> rasDecode(md5(pwd)) ->
        // {server} pwd= rsaEncode(body.pwd) ->md5(pwd+salt~Random(8,str)) -> savePwd
        // 好像后端不需要解密, 直接保存就行了


        // 先去校验用户名是否存在, 如果存在, 不能重复注册
        User u = userRepository.findUserByUsernameAndDeletedIsFalse(userInfo.getName());
        if (u != null) {
            log.error("username is registered: [{}]", userInfo.getName());
            userInfo.setName(userInfo.getName() + "_" + userInfo.getId());
        }
//        String registerRBPassword = userInfo.getPassword();
//        String salt = RandomUtil.randomString(8);
//        String password = SecureUtil.md5(registerRBPassword + salt);


        User user = User.builder()
                .username(userInfo.getName())
                .nickname(userInfo.getName())
                .build();
        user.setUserInfo(UserInfo.builder()
                .avatar(userInfo.getAvatar_url())//cdn
                .level(0)
                .build());
        User save = userRepository.save(user);
        OauthUserRef oauthUserRef = OauthUserRef.builder().userId(save.getId())
                .oauthId(userInfo.getId())
                .oauthNodeId(userInfo.getNode_id())
                .oauthType(OauthType.Github)
                .oauthAvatar(userInfo.getAvatar_url())
                .build();
        oauthUserRefRepository.save(oauthUserRef);
        // 注册一个新用户, 写一条记录到数据表中
        log.info("github register user success: [{}], [{}],[{}]", user.getUsername(), user.getId(), user.getPassword());
        // 生成 token 并返回
        LoginUserInfo loginUserInfo = LoginUserInfo.builder()
                .username(user.getUsername())
                .id(user.getId())
                .nickname(user.getNickname()).build();
        var expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());
        return TokenUtil.generateToken(loginUserInfo, expireDate);
    }
}
