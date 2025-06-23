package timeeat.client.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class OauthProperties {

    private final String clientId;
    private final String redirectUri;
}
