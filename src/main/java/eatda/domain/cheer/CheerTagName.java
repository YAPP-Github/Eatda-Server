package eatda.domain.cheer;

import lombok.Getter;

@Getter
public enum CheerTagName {

    OLD_STORE_MOOD(CheerTagCategory.MOOD, "노포 감성"),
    ENERGETIC(CheerTagCategory.MOOD, "활기찬"),
    GOOD_FOR_DATING(CheerTagCategory.MOOD, "데이트하기좋은"),
    QUIET(CheerTagCategory.MOOD, "조용한"),
    GOOD_FOR_DRINKING(CheerTagCategory.MOOD, "술 땡기는"),
    INSTAGRAMMABLE(CheerTagCategory.MOOD, "인스타 감성"),
    GOOD_FOR_FAMILY(CheerTagCategory.MOOD, "부모님과 가기 좋은"),
    YOUTUBE_FAMOUS(CheerTagCategory.MOOD, "유튜버 맛집"),

    GROUP_RESERVATION(CheerTagCategory.PRACTICAL, "단체 예약"),
    LARGE_PARKING(CheerTagCategory.PRACTICAL, "넓은 주차장"),
    CLEAN_RESTROOM(CheerTagCategory.PRACTICAL, "깔끔한 화장실"),
    PET_FRIENDLY(CheerTagCategory.PRACTICAL, "반려동물 동반 가능"),
    LATE_NIGHT(CheerTagCategory.PRACTICAL, "늦게까지 영업"),
    NEAR_SUBWAY(CheerTagCategory.PRACTICAL, "지하철과 가까운"),
    MANY_NEARBY_ATTRACTIONS(CheerTagCategory.PRACTICAL, "주변에 놀거리가 많은"),
    ;

    private final CheerTagCategory type;
    private final String displayName;

    CheerTagName(CheerTagCategory type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public enum CheerTagCategory {
        MOOD, PRACTICAL
    }
}
