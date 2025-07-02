package timeeat.fixture;

import org.springframework.stereotype.Component;
import timeeat.domain.member.Member;
import timeeat.enums.InterestArea;
import timeeat.repository.member.MemberRepository;

@Component
public class MemberGenerator {

    private static final String DEFAULT_INTEREST_AREA = InterestArea.SEONGBUK.getAreaName();

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
        return memberRepository.save(new Member(socialId, nickname, phoneNumber, DEFAULT_INTEREST_AREA, true));
    }
}
