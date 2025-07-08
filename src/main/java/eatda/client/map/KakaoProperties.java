package eatda.client.map;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private final String apiKey;

    public KakaoProperties(String apiKey) {
        validateApiKey(apiKey);
        this.apiKey = apiKey;
    }

    private void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be null or blank");
        }
    }
}
