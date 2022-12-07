package link.dwsy.ddl.XO.Enum.User;


import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.entity.Infinity.Infinity;

/**
 * @Author Dwsy
 * @Date 2022/9/4
 */

public enum UserActiveType {
    Check_In,//签到

    Browse_Article,//浏览文章

    Browse_QA,//浏览问题
    //-------------

    Comment_Article,//评论文章

    Comment_Article_Comment,//评论文章评论

    UP_Article,//点赞文章

    UP_Article_Comment,//点赞文章评论


    Question_Answer,//

    Question_Or_Answer_Comment,//回复问题或回答

    Question_Comment,//回复问题

    Comment_Answer,//回复回答
    UP_Question,//点赞问题

    UP_Question_Answer,//点赞问题回答

    Accepted_Question_Answer,//采纳问题回答

    Comment_Tweet,//回复推文
    Reply_Comment_Tweet,//回复推文回复
    Reply_Reply_Comment_Tweet,//回复推文二级回复
    Thumb_Tweet//点赞tweet
    ;

    public static UserActiveType Converter(CommentType commentType, long parentCommentId) {
//        comment,up, down,cancel
        switch (commentType) {
            case comment:
                if (parentCommentId == 0) {
                    return Comment_Article;
                } else {
                    return Comment_Article_Comment;
                }
            case up:
                if (parentCommentId == -1) {
                    return UP_Article;
                } else {
                    return UP_Article_Comment;
                }
            default:
                return null;
        }

    }

    public static UserActiveType Converter(AnswerType answerType, long parentAnswerId) {

        switch (answerType) {
            case answer:
                return Question_Answer;
            case up:
                if (parentAnswerId == -1) {
                    return UP_Question;
                } else {
                    return UP_Question_Answer;
                }
            case answer_comment:
                return Comment_Answer;
            case comment:
                return Question_Comment;
            default:
                return null;
        }
    }


    public static UserActiveType Converter(Infinity infinity) {
        switch (infinity.getType()) {
            case TweetCommentOrReply:
                if (infinity.getParentUserId() == null) {
                    return Comment_Tweet;
                } else if (infinity.getRefId() != null) {
                    return Reply_Reply_Comment_Tweet;
                }
                return Reply_Comment_Tweet;
            case upTweet:
                return Thumb_Tweet;
        }
        return null;
    }
}
