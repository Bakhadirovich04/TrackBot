package uz.husan.signup.entity.enums;

public enum Role {
    USER("ROLE_USER"),
    CHEF("ROLE_CHEF"),;

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
