package eatda.client.map;

import eatda.domain.store.Coordinates;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@EnableConfigurationProperties(KakaoProperties.class)
public class MapClient {

    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public MapClient(RestClient.Builder restClient, KakaoProperties properties) {
        this.restClient = restClient
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, new MapServerErrorHandler())
                .build();
        this.kakaoProperties = properties;
    }

    public List<MapClientStoreSearchResult> searchStores(String query) {
        return restClient.get()
                .uri("https://dapi.kakao.com/v2/local/search/keyword.json", builder -> builder
                        .queryParam("query", query)
                        .queryParam("category", "FD6")
                        .queryParam("rect", "%s,%s,%s,%s".formatted(
                                Coordinates.getMinLongitude(), Coordinates.getMinLatitude(),
                                Coordinates.getMaxLongitude(), Coordinates.getMaxLatitude()))
                        .queryParam("page", 1)
                        .queryParam("size", 15)
                        .queryParam("sort", "accuracy")
                        .build())
                .header("Authorization", "KakaoAK " + kakaoProperties.getApiKey())
                .retrieve()
                .body(MapClientStoreSearchResults.class)
                .results();
    }
}
