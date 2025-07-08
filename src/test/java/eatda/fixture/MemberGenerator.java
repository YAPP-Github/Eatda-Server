package eatda.fixture;

import eatda.domain.member.Member;
import eatda.enums.InterestArea;
import eatda.repository.member.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberGenerator {

    private final MemberRepository memberRepository;

    public MemberGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member generate(String socialId) {
        return memberRepository.save(new Member(socialId, "nickname"));
    }

    public Member generate(String socialId, String nickname) {
        return memberRepository.save(new Member(socialId, nickname));
    }

    public Member generateRegisteredMember(String socialId, String nickname, String phoneNumber) {
        return memberRepository.save(new Member(socialId, nickname, phoneNumber, true));
    }
}
