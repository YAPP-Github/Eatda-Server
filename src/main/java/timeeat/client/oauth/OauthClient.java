package timeeat.client.oauth;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OauthClient {

    private final String clientId;
    private final String redirectUri;

    public OauthClient(@Value("${oauth.clientId}") String clientId, @Value("${oauth.redirectUri}") String redirectUri) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    public URI getOauthLoginUrl() {
        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build()
                .toUri();
    }
}
