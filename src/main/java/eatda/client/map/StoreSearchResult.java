package eatda.client.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreSearchResult(
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

    public boolean isFoodStore() {
        return "FD6".equals(categoryGroupCode);
    }

    public boolean isInSeoul() {
        if (lotNumberAddress == null || lotNumberAddress.isBlank()) {
            return false;
        }
        return lotNumberAddress.trim().startsWith("서울");
    }
}
