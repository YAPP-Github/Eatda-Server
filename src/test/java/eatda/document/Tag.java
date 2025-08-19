package eatda.document;

public enum Tag {

    AUTH_API("Auth API"),
    MEMBER_API("Member API"),
    STORE_API("Store API"),
    CHEER_API("Cheer API"),
    STORY_API("Story API"),
    ARTICLE_API("Article API"),
    IMAGE_API("Image API"),
    ;

    private final String displayName;

    Tag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
