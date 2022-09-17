package link.dwsy.ddl.XO.Enum.User;

public enum PointsType {

    Check_In(2),//签到
    Publish_Article(10),

    Browse_Article(1),//浏览文章

    Browse_QA(1),//浏览问题

    Comment_Article(1),//评论文章

    Comment_Article_Comment(1),//评论文章评论

    UP_Article(1),//点赞文章

    UP_Article_Comment(1),//点赞文章评论

    Publish_Question(10),

    Answer_Question(2),//回答问题 or 问题回答

    Answer_Accepted(10),

    UP_Question(1),//点赞问题

    UP_Question_Answer(1),//点赞问题回答

    Reward(0),//打赏

    ;

    public int point;
    PointsType(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }
}
