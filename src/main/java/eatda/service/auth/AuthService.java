package eatda.service.auth;

import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import eatda.client.oauth.OauthClient;
import eatda.client.oauth.OauthMemberInformation;
import eatda.client.oauth.OauthToken;
import eatda.controller.auth.LoginRequest;
import eatda.controller.member.MemberResponse;
import eatda.domain.member.Member;
import eatda.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        OauthToken oauthToken = oauthClient.requestOauthToken(request.code(), request.origin());
        OauthMemberInformation oauthInformation = oauthClient.requestMemberInformation(oauthToken);

        Optional<Member> optionalMember = memberRepository.findBySocialId(Long.toString(oauthInformation.socialId()));
        boolean isFirstLogin = optionalMember.isEmpty();
        return new MemberResponse(
                optionalMember.orElseGet(() -> memberRepository.save(oauthInformation.toMember())),
                isFirstLogin);
    }
}
