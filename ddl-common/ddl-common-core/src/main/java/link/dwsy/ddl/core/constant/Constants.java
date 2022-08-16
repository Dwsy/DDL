package link.dwsy.ddl.core.constant;

/**
 * @Author Dwsy
 * @Date 2022/8/15
 */

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public class Constants {
    /**
     * RSA 公钥
     */
    private static final String PUBLIC_KEY =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA5hJHATNnoqYHI6M2OTA1\n" +
                    "qX30KJKrKQcpBCvMXipV9oTQcMwDLYwSbI/uL1PQawC7sxPb9PKYtZf/mNT6W79I\n" +
                    "HB+s6XJZrW2z++KYKiX8KIIXN1Qimf6jmEPGpfcOA1midPlVN8vRDG3FXVoKB/Ks\n" +
                    "7X0iDiHf2FEc/s/0mQYlNH6jVCFyrRvAiGqGMegb1hqvSrl56fvjUkCr04jZhqJ6\n" +
                    "+FyEnRWGqex94OcYHqEjZCy2TI2WpcnIfNUeOHGYTZvfiek1VafzwTzd9G1Wr4xW\n" +
                    "6aNJ/GmlQbUyWwliqqQp6NXnTzRDORycIJD3JyMOpNmlkJMmNw5C3DN/vA753LbU\n" +
                    "GhBLiu8yBVNLLecXhBV4bIayCIvRZj0CyxrjHwUx7axopDK6BnzVNeEe+HkPi0S7\n" +
                    "kfPWbllcRa3jZ5RJOeX4z7Q67Gu5aUeJmrYw/IfiXUEy6qkzYy6ZQ5dY8fH1bbXK\n" +
                    "R/vkiUJ8l3AF2dLfVq42ayaNgt8zh8GjkrsU03vc8W+m0kw7NmBGUpxYfBkJcP1Q\n" +
                    "/Z4YjPlUzGAPj8xbfD17hFUQX2+GqG8INK5dCUkey+yx+au8DjSFMEnKNlDg6uka\n" +
                    "2t16LSt2AT54ImnlqGt04a/fXruZvfKtz3CN5RmaSrTv5JcvJtjqtlz8E0WDXVAS\n" +
                    "HcE948KL4g0WaGqwpoNOuL8CAwEAAQ==";
    /**
     * 授权中心的 service-id
     */
    public static final String AUTHORITY_CENTER_SERVICE_ID = "ddl-authority-center";
    /**
     * JWT 中存储用户信息的 key
     */
    public static final String JWT_USER_INFO_KEY = "ddl-user";
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功状态
     */
    public static final String LOGIN_SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    public static final String LOGIN_FAIL_STATUS = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = {"com.ruoyi"};

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.ruoyi.common.core.utils.file"};
}