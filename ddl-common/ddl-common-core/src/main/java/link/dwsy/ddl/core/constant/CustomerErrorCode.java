package link.dwsy.ddl.core.constant;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

public enum CustomerErrorCode {

    /**
     * Success 0
     * Code
     * 1xx 用户
     * 2xx 文章
     * 3xx 评论
     * 4xx 参数
     */


    UserNotLogin(101, "用户未登录"),

    UserNotExist(103, "用户不存在"),
    UserLevelLow(104, "用户等级不足"),
    ParamError(400, "请求参数错误"),

    NotFound(201,"文章不存在");

    private int code;

    private String message;

    CustomerErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}