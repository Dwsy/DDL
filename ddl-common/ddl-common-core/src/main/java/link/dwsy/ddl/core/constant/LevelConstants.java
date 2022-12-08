package link.dwsy.ddl.core.constant;

/**
 * @Author Dwsy
 * @Date 2022/12/6
 */

public class LevelConstants {
    public static int getUserLevelByExperience(int exp) {
        if (exp >= 28800) {
            return 6;
        }
        if (exp >= 10800) {
            return 5;
        }
        if (exp >= 4500) {
            return 4;
        }
        if (exp >= 1500) {
            return 3;
        }
        if (exp >= 200) {
            return 2;
        }
        if (exp >= 0) {
            return 1;
        }
        return 0;

    }
}
