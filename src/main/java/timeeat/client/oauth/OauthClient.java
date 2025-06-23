package timeeat.client.oauth;

import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@EnableConfigurationProperties(OauthProperties.class)
public class OauthClient {

    private final OauthProperties properties;

    public OauthClient(OauthProperties oauthProperties) {
        this.properties = oauthProperties;
    }

    public URI getOauthLoginUrl() {
        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("response_type", "code")
                .build()
                .toUri();
    }
}
