package link.dwsy.ddl.XO.Enum;

public enum Gender {
    MALE("男"), FEMALE("女");
    private String value;
    private Gender(String value) {
        this.value = value;
    }
}
