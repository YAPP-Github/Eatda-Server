package eatda.domain.cheer;

import lombok.Getter;

@Getter
public enum CheerTagName {

    OLD_STORE_MOOD(CheerTagCategory.MOOD),
    ENERGETIC(CheerTagCategory.MOOD),
    GOOD_FOR_DATING(CheerTagCategory.MOOD),
    QUIET(CheerTagCategory.MOOD),
    GOOD_FOR_DRINKING(CheerTagCategory.MOOD),
    INSTAGRAMMABLE(CheerTagCategory.MOOD),
    GOOD_FOR_FAMILY(CheerTagCategory.MOOD),
    YOUTUBE_FAMOUS(CheerTagCategory.MOOD),

    GROUP_RESERVATION(CheerTagCategory.PRACTICAL),
    LARGE_PARKING(CheerTagCategory.PRACTICAL),
    CLEAN_RESTROOM(CheerTagCategory.PRACTICAL),
    PET_FRIENDLY(CheerTagCategory.PRACTICAL),
    LATE_NIGHT(CheerTagCategory.PRACTICAL),
    NEAR_SUBWAY(CheerTagCategory.PRACTICAL),
    MANY_NEARBY_ATTRACTIONS(CheerTagCategory.PRACTICAL);
    ;

    private final CheerTagCategory type;

    CheerTagName(CheerTagCategory type) {
        this.type = type;
    }

    public enum CheerTagCategory {
        MOOD, PRACTICAL
    }
}
