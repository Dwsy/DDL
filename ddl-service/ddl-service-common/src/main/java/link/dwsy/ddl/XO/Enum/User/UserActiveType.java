package link.dwsy.ddl.XO.Enum.User;


import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;

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


    Answer_Question,//回答问题 or 问题回答

    CommentP_Question_Answer,//评论回答
    UP_Question,//点赞问题

    UP_Question_Answer,//点赞问题回答



    ;

    public static UserActiveType Converter(CommentType commentType,long parentCommentId) {
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
                if (parentAnswerId == 0) {
                    return Answer_Question;
                } else {
                    return CommentP_Question_Answer;
                }
            case up:
                if (parentAnswerId == -1) {
                    return UP_Question;
                } else {
                    return UP_Question_Answer;
                }
            default:
                return null;
        }
    }
}
