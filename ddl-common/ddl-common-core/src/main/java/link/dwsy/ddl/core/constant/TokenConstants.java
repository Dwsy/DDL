package link.dwsy.ddl.core.constant;

/**
 * Token的Key常量
 * 
 * @author ruoyi
 */
public class TokenConstants
{
    /**
     * 令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";

    public static final String REDIS_TOKEN_BLACKLIST_KEY = "Token:Blacklist:";

    public static final String REDIS_TOKEN_ACTIVE_TIME_KEY = "Token:ActiveTime:";
}
