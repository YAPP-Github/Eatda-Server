package eatda.client.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MapClientStoreSearchResult(
        @JsonProperty("id") String kakaoId,
        @JsonProperty("category_group_code") String categoryGroupCode,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("phone") String phoneNumber,
        @JsonProperty("place_name") String name,
        @JsonProperty("place_url") String placeUrl,
        @JsonProperty("address_name") String lotNumberAddress,
        @JsonProperty("road_address_name") String roadAddress,
        @JsonProperty("y") double latitude,
        @JsonProperty("x") double longitude
) {

    private static final Map<String, StoreCategory> PREFIX_TO_CATEGORY = Map.of(
            "음식점 > 한식", StoreCategory.KOREAN,
            "음식점 > 중식", StoreCategory.CHINESE,
            "음식점 > 일식", StoreCategory.JAPANESE,
            "음식점 > 양식", StoreCategory.WESTERN,
            "음식점 > 카페", StoreCategory.CAFE,
            "음식점 > 간식 > 제과,베이커리", StoreCategory.CAFE
    );

    public boolean isFoodStore() {
        return "FD6".equals(categoryGroupCode);
    }

    public boolean isInSeoul() {
        if (lotNumberAddress == null || lotNumberAddress.isBlank()) {
            return false;
        }
        return lotNumberAddress.trim().startsWith("서울");
    }

    public StoreCategory getStoreCategory() {
        if (categoryName == null) {
            return StoreCategory.OTHER;
        }

        return PREFIX_TO_CATEGORY.entrySet()
                .stream()
                .filter(entry -> categoryName.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(StoreCategory.OTHER);
    }

    public Store toStore() {
        return Store.builder()
                .kakaoId(kakaoId)
                .category(getStoreCategory())
                .phoneNumber(phoneNumber)
                .name(name)
                .placeUrl(placeUrl)
                .roadAddress(roadAddress)
                .lotNumberAddress(lotNumberAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public StoreSearchResult toDomain() {
        return new StoreSearchResult(
                kakaoId,
                getStoreCategory(),
                phoneNumber,
                name,
                placeUrl,
                lotNumberAddress,
                roadAddress,
                latitude,
                longitude
        );
    }
}
