package link.dwsy.ddl.constants.task;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */

public enum RedisInfinityRecordHashKey {
    view("view"),
    up("up"),
    comment("comment"),
    collect("collect"),
    share("share"),
    quote("quote")
    ;
    private final String key;

    RedisInfinityRecordHashKey(String key) {
        this.key = key;
    }
}
