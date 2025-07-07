package eatda.client.oauth;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@EnableConfigurationProperties(OauthProperties.class)
public class OauthClient {

    private final RestClient restClient;
    private final OauthProperties properties;

    public OauthClient(RestClient.Builder restClientBuilder,
                       OauthProperties oauthProperties) {
        this.restClient = restClientBuilder
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, new OauthServerErrorHandler())
                .build();
        this.properties = oauthProperties;
    }

    public URI getOauthLoginUrl(String origin) {
        validateOrigin(origin);

        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", createRedirectUri(origin))
                .queryParam("response_type", "code")
                .build()
                .toUri();
    }

    public OauthToken requestOauthToken(String code, String origin) {
        validateOrigin(origin);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getClientId());
        body.add("redirect_uri", createRedirectUri(origin));
        body.add("code", code);

        return restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(OauthToken.class);
    }

    private void validateOrigin(String origin) {
        if (!properties.isAllowedOrigin(origin)) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_ORIGIN);
        }
    }

    private String createRedirectUri(String origin) {
        return UriComponentsBuilder.fromUriString(origin)
                .path(properties.getRedirectPath())
                .build()
                .toString();
    }

    public OauthMemberInformation requestMemberInformation(OauthToken token) {
        return restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(headers -> headers.setBearerAuth(token.accessToken()))
                .retrieve()
                .body(OauthMemberInformation.class);
    }
}
