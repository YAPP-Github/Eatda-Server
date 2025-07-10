package eatda.service.member;

import eatda.controller.member.MemberResponse;
import eatda.controller.member.MemberUpdateRequest;
import eatda.domain.member.Member;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void validateNickname(String nickname, long memberId) {
        Member member = memberRepository.getById(memberId);
        validateNicknameNotDuplicate(member, nickname);
    }

    @Transactional(readOnly = true)
    public void validatePhoneNumber(String phoneNumber, long memberId) {
        Member member = memberRepository.getById(memberId);
        validatePhoneNumberNotDuplicate(member, phoneNumber);
    }

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
