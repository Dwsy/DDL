package link.dwsy.ddl.XO.Enum.User;

public enum Gender {
    MAIL("男"), FMAIL("女");
    private final String value;
    private Gender(String value) {
        this.value = value;
    }
}
