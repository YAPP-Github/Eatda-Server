package timeeat.service.member;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import timeeat.client.oauth.OauthClient;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final OauthClient oauthClient;

    public URI getOauthLoginUrl() {
        return oauthClient.getOauthLoginUrl();
    }
}
