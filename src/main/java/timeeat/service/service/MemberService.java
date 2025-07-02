package timeeat.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.member.MemberUpdateRequest;
import timeeat.domain.member.Member;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;
import timeeat.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse update(long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.getById(memberId);
        validatePhoneNumberNotDuplicate(member, request.phoneNumber());
        validateNicknameNotDuplicate(member, request.nickname());

        Member memberUpdater = request.toMemberUpdater();
        member.update(memberUpdater);
        return new MemberResponse(member);
    }

    private void validateNicknameNotDuplicate(Member member, String newNickname) {
        if (!member.isSameNickname(newNickname) && memberRepository.existsByNickname(newNickname)) {
            throw new BusinessException(BusinessErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePhoneNumberNotDuplicate(Member member, String newPhoneNumber) {
        if (!member.isSameMobilePhoneNumber(newPhoneNumber)
                && memberRepository.existsByMobilePhoneNumberValue(newPhoneNumber)) {
            throw new BusinessException(BusinessErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }
}
