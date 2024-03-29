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
     * 5xx 信息
     */


    UserNotLogin(101, "用户未登录"),

    UserNotExist(103, "用户不存在"),
    UserTokenExpired(104, "用户令牌已过期"),
    //    token解析失败
    TokenParseError(105, "token解析失败"),
    UserLevelLow(110, "用户等级不足"),

    TokenNotFound(111, "token未找到"),

    OldPWDEqualsNewPWD(119, "新旧密码不能相同"),

    OldPWDWrong(120, "旧密码错误"),

    UserAlreadyFollowed(130, "已关注用户"),
    UserNotFollowed(131, "未关注用户"),

    FOLLOW_SELF(134, "不能关注自己"),
    UserCancellation(150, "用户已注销"),

    ArticleNotFound(201, "文章不存在"),
    GroupNotFound(202, "分组不出存在"),

    ArticleCommentIsClose(204, "文章不允许评论"),

    ArticleCommentNotIsFirst(205, "评论不是一级评论"),
    ArticleCommentNotFount(206, "评论不存在"),

    ArticleGroupAlreadyExists(207, "分组已存在"),

    ArticleTagAlreadyExists(208, "标签已存在"),

    ArticleGroupNotFound(209, "分组不存在"),
    ArticleTagNotFound(210, "标签不存在"),
    ArticleGroupNotBelongToUser(211, "权限不足"),

    ArticleGroupNotEmpty(212, "分组不为空"),//todo 强制删除好像也不是不行
    UserCollectionGroupNotExist(213, "收藏分组不存在"),
    UserCollectionNotExist(215, "未收藏"),

    UserCollectionAlreadyExist(214, "已收藏"),

    ArticleStateError(230, "文章状态错误"),

    ArticleVersionNotFound(230, "文章历史版本不存在"),

    ArticleVersionError(233, "文章历史版本错误"),

    QuestionAnswerOrCommentNotFount(250, "问题或评论不存在"),


    ParamError(400, "请求参数错误"),
    BodyError(401, "请求体错误"),

    ChannelNotExist(501, "频道不存在"),
    ChannelNotJoin(502, "未加入频道"),

    ChannelNotOwner(504, "不是频道所有者"),
    ChannelMessageSendError(505, "消息发送失败"),


    QuestionNotFound(601, "问题不存在"),
    AnswerNotFound(602, "答案不存在"),

    QuestionAnswerNotBelongToUser(603, "不是你的答案"),

    QuestionAnswerNotBelongToQuestion(604, "不是你的问题"),

    QuestionNotFoundOrClose(605, "问题不存在或不已关闭"),

    YouCanTInviteYourself(606, "不能邀请自己"),

    TheUserHasBeenInvitedToAnswer(606, "已经邀请了用户回答"),

    QuestionVersionNotFound(620, "问题历史版本不存在"),

    WordNotFount(10001, "未找到该词"),

    WordExist(10002, "该词已存在"),
    CaptchaWrong(100004, "验证码错误"),
    EmailEqualsOld(100005, "邮箱未更改"),
    EmailExists(100006, "邮箱已被绑定"),
    INFINITY_CLUB_NOT_EXIST(704, "圈子不存在"),
    INFINITY_TOPIC_NOT_EXIST(703, "主题不存在"),
    INFINITY_TOPIC_EXIST(703, "主题已存在"),

    INFINITY_TOPIC_NOT_BELONG_TO_USER(705, "主题不属于你"),

    INFINITY_NOT_EXIST(710, "动态不存在"),

    INFINITY_TOPIC_FOLLOWED(720, "已关注主题"),

    INFINITY_TOPIC_UNFOLLOWED(721, "未关注主题"),

    INFINITY_CLUB_FOLLOWED(722, "已关注圈子"),

    INFINITY_CLUB_UNFOLLOWED(723, "未关注圈子"),
    GITHUB_OAUTH_ERROR(2000, "GITHUB_OAUTH_ERROR"),
    SensitiveWordsExistInTheContent(1234, "内容存在敏感词请重新编辑"),
    UserNotAdmin(4001,"权限不足" );


    private final int code;

    private final String message;

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