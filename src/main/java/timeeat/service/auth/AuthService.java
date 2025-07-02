package timeeat.service.auth;

import java.net.URI;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import timeeat.client.oauth.OauthClient;
import timeeat.client.oauth.OauthMemberInformation;
import timeeat.client.oauth.OauthToken;
import timeeat.controller.auth.MemberLoginRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OauthClient oauthClient;
    private final MemberRepository memberRepository;

    public URI getOauthLoginUrl(String origin) {
        return oauthClient.getOauthLoginUrl(origin);
    }

    @Transactional
    public MemberResponse login(LoginRequest request) {
        OauthToken oauthToken = oauthClient.requestOauthToken(request.code());
        OauthMemberInformation oauthInformation = oauthClient.requestMemberInformation(oauthToken);

        Optional<Member> optionalMember = memberRepository.findBySocialId(Long.toString(oauthInformation.socialId()));
        boolean isFirstLogin = optionalMember.isEmpty();
        return new MemberResponse(
                optionalMember.orElseGet(() -> memberRepository.save(oauthInformation.toMember())),
                isFirstLogin);
    }
}
