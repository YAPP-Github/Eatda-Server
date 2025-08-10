package eatda.service.auth;

import eatda.client.oauth.OauthClient;
import eatda.client.oauth.OauthMemberInformation;
import eatda.client.oauth.OauthToken;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final OauthClient oauthClient;

    public URI getOauthLoginUrl(String origin) {
        return oauthClient.getOauthLoginUrl(origin);
    }

    public OauthMemberInformation getOAuthInformation(String code, String origin) {
        OauthToken oauthToken = oauthClient.requestOauthToken(code, origin);
        return oauthClient.requestMemberInformation(oauthToken);
    }
}
