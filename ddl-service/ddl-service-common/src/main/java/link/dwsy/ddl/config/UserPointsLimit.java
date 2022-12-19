package link.dwsy.ddl.config;

import link.dwsy.ddl.XO.Enum.User.PointsType;

public class UserPointsLimit {

    public static void main(String[] args) {
        System.out.println(PointsType.Browse_QA.ordinal());
    }
    public static int get(PointsType pointsType) {
        switch (pointsType) {//todo value file conf
            case Check_In:
                return 2;
            case Publish_Article:
                return 30;

            case Browse_Article:
                return 10;

            case Browse_QA:
                return 10;

            case Comment_Article:
                return 10;

            case Comment_Article_Comment:
                return 10;

            case UP_Article:
                return 5;

            case UP_Article_Comment:
                return 5;

            case Publish_Question:
                return 30;

            case Answer_Question:
                return 50;

            case Answer_Accepted:
                return 20;

            case UP_Question:
                return 10;

            case UP_Question_Answer:
                return 10;

            case Reward:
                return 999999;

        }
        return 0;
    }
}
