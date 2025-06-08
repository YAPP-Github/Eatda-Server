package timeeat.document;

public enum Tag {

    MEMBER_API("Member API"), // Example (추후 삭제)
    ;

    private final String displayName;

    Tag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
