package link.dwsy.ddl.XO.Enum.User;

public enum Gender {
    MALE("男"), FEMALE("女");
    private final String value;
    private Gender(String value) {
        this.value = value;
    }
}
