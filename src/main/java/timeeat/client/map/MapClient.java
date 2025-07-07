package timeeat.client.map;

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
                .uri("https://dapi.kakao.com/v2/local/search/keyword.json?query={query}&category={category}&page=1&size=15&sort=accuracy",
                        query, "FD6")
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .body(ShopSearchResults.class);
    }
}
