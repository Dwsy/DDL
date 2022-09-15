package link.dwsy.ddl.constant;

/**
 * <h1>网关常量定义</h1>
 */
public class GatewayConstant {
//todo 待修改 优化
    /**
     * 登录 uri
     */
    public static final String LOGIN_URI = "/ddl/login";
    /**
     * 注册 uri
     */
    public static final String REGISTER_URI = "/ddl/register";

    public static final String LOGOUT_URI = "/ddl/logout";

    public static final String REFRESH_URI = "/ddl/refresh";

    /**
     * 去授权中心拿到登录 token 的 uri 格式化接口
     */
    public static final String AUTHORITY_CENTER_TOKEN_URL_FORMAT =
            "http://%s:%s/ddl-authority-center/authority/token";

    /**
     * 去授权中心注册并拿到 token 的 uri 格式化接口
     */
    public static final String AUTHORITY_CENTER_REGISTER_URL_FORMAT =
            "http://%s:%s/ddl-authority-center/authority/register";

    public static final String AUTHORITY_LOGOUT_URL_FORMAT =
            "http://%s:%s/ddl-authority-center/authority/logut";

    public static final String AUTHORITY_REFRESH_URL_FORMAT =
            "http://%s:%s/ddl-authority-center/authority/refresh";
}
