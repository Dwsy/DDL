package link.dwsy.ddl.xo.Enum;

public enum Gender {
    MAIL("男"), FMAIL("女");
    private String value;
    private Gender(String value) {
        this.value = value;
    }
}
