package timeeat.client.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "oauth")
@RequiredArgsConstructor
public class OauthProperties {

    private final String clientId;
    private final String redirectUri;
}
