package eatda.document;

public enum Tag {

    AUTH_API("Auth API"),
    MEMBER_API("Member API"),
    STORE_API("Store API"),;

    private final String displayName;

    Tag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
