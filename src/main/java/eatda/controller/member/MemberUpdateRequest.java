package eatda.controller.member;

import eatda.domain.member.Member;
import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(@NotBlank String nickname,
                                  @NotBlank String phoneNumber,
                                  @NotBlank String interestArea,
                                  boolean optInMarketing) {

    public Member toMemberUpdater() {
        return new Member(nickname, phoneNumber, interestArea, optInMarketing);
    }
}
