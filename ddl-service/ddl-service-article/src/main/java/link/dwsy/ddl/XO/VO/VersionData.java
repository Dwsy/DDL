package link.dwsy.ddl.XO.VO;

/**
 * @Author Dwsy
 * @Date 2022/11/18
 */

public class VersionData {
    public String title;
    public String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public VersionData(String title, String date) {
        this.title = title;
        this.date = date;
    }
}