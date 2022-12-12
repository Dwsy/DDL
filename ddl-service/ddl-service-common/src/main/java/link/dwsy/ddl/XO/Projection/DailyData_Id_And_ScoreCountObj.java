package link.dwsy.ddl.XO.Projection;

/**
 * @Author Dwsy
 * @Date 2022/12/11
 */

public class DailyData_Id_And_ScoreCountObj implements DailyData_Id_And_ScoreCount {
    private int scoreCount;
    private long id;

    public DailyData_Id_And_ScoreCountObj( long id,int scoreCount) {
        this.scoreCount = scoreCount;
        this.id = id;
    }

    public int getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(int scoreCount) {
        this.scoreCount = scoreCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

