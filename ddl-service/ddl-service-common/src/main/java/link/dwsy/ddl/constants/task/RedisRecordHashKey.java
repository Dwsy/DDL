package link.dwsy.ddl.constants.task;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */

public enum RedisRecordHashKey {
    view("view"),
    up("up"),
    down("down"),
    comment("comment"),
    answer("answer"),
    collect("collect"),
    share("share"),
    ;
    private final String key;

    RedisRecordHashKey(String key) {
        this.key = key;
    }
}
