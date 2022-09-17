package link.dwsy.ddl.XO.Enum.User;


import link.dwsy.ddl.XO.Enum.Article.CommentType;

/**
 * @Author Dwsy
 * @Date 2022/9/4
 */

public enum UserActiveType {
    Check_In,//签到

    Browse_Article,//浏览文章

    Browse_QA,//浏览问题

    Comment_Article,//评论文章

    Comment_Article_Comment,//评论文章评论

    UP_Article,//点赞文章

    UP_Article_Comment,//点赞文章评论


    Answer_Question,//回答问题 or 问题回答

    UP_Question,//点赞问题

    UP_Question_Answer,//点赞问题回答



    ;

    public static UserActiveType get(CommentType commentType) {
//        comment,up, down,cancel
        switch (commentType) {
            case comment:
                return Comment_Article;
            case up:
                return UP_Article_Comment;

            default:
                return null;
        }

    }
}
