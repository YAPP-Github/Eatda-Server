package eatda.service.auth;

import eatda.client.oauth.OauthMemberInformation;
import eatda.controller.member.MemberResponse;
import eatda.domain.member.Member;
import eatda.repository.member.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse login(OauthMemberInformation oauthInformation) {
        Optional<Member> optionalMember = memberRepository.findBySocialId(Long.toString(oauthInformation.socialId()));
        boolean isFirstLogin = optionalMember.isEmpty();
        return new MemberResponse(
                optionalMember.orElseGet(() -> memberRepository.save(oauthInformation.toMember())),
                isFirstLogin);
    }
}
