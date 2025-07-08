package eatda.client.map;

import eatda.domain.store.Coordinates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MapClient {

    private final RestClient restClient;
    private final String apiKey; // TODO NotBlank 검증

    public MapClient(RestClient.Builder restClient, @Value("${kakao.api-key}") String apiKey) {
        this.restClient = restClient
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, new MapServerErrorHandler())
                .build();
        this.apiKey = apiKey;
    }

    public ShopSearchResults searchShops(String query) {
        return restClient.get()
                .uri(builder -> builder
                        .path("https://dapi.kakao.com/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("category", "FD6")
                        .queryParam("rect", "%s,%s,%s,%s".formatted(
                                Coordinates.getMinLongitude(), Coordinates.getMinLatitude(),
                                Coordinates.getMaxLongitude(), Coordinates.getMaxLatitude()))
                        .queryParam("page", 1)
                        .queryParam("size", 15)
                        .queryParam("sort", "accuracy")
                        .build())
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .body(ShopSearchResults.class);
    }
}
