package timeeat.client.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopSearchResult(
        @JsonProperty("id") String kakaoId,
        @JsonProperty("category_group_code") String categoryGroupCode,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("phone") String phoneNumber,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("place_url") String placeUrl,
        @JsonProperty("address_name") String addressName,
        @JsonProperty("road_address_name") String roadAddressName,
        @JsonProperty("y") double latitude,
        @JsonProperty("x") double longitude
) {
}
