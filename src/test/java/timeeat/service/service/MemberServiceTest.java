package timeeat.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.member.MemberUpdateRequest;
import timeeat.domain.member.Member;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;
import timeeat.service.BaseServiceTest;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    private MemberService memberService;

    @Nested
    class Update {

        @Test
        void 회원_정보를_수정할_수_있다() {
            Member member = memberGenerator.generate("123");
            MemberUpdateRequest request = new MemberUpdateRequest("update-nickname", "01012345678", "성북구", true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(member.getId()),
                    () -> assertThat(response.isSignUp()).isFalse(),
                    () -> assertThat(response.nickname()).isEqualTo("update-nickname"),
                    () -> assertThat(response.phoneNumber()).isEqualTo("01012345678"),
                    () -> assertThat(response.interestArea()).isEqualTo("성북구"),
                    () -> assertThat(response.optInMarketing()).isTrue()
            );
        }

        @Test
        void 중복된_닉네임이_있으면_예외가_발생한다() {
            Member existMember = memberGenerator.generate("123", "duplicate-nickname");
            Member updatedMember = memberGenerator.generate("456");
            MemberUpdateRequest request =
                    new MemberUpdateRequest(existMember.getNickname(), "01012345678", "성북구", true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.update(updatedMember.getId(), request));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_NICKNAME);
        }

        @Test
        void 기존의_닉네임과_동일하면_있으면_정상적으로_회원_정보가_수정된다() {
            Member member = memberGenerator.generate("123", "duplicate-nickname");
            MemberUpdateRequest request =
                    new MemberUpdateRequest(member.getNickname(), "01012345678", "성북구", true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertThat(response.nickname()).isEqualTo(request.nickname());
        }

        @Test
        void 중복된_전화번호가_있으면_예외가_발생한다() {
            String phoneNumber = "01012345678";
            Member existMember = memberGenerator.generate("123", "nickname1");
            memberService.update(existMember.getId(),
                    new MemberUpdateRequest("nickname1", phoneNumber, "성북구", true));
            Member updatedMember = memberGenerator.generate("456", "nickname2");
            MemberUpdateRequest request =
                    new MemberUpdateRequest("new-nickname", phoneNumber, "성북구", true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> memberService.update(updatedMember.getId(), request));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        @Test
        void 기존의_전화번호와_동일하면_정상적으로_회원_정보가_수정된다() {
            String phoneNumber = "01012345678";
            Member member = memberGenerator.generate("123", "nickname1");
            memberService.update(member.getId(),
                    new MemberUpdateRequest("nickname1", phoneNumber, "성북구", true));
            MemberUpdateRequest request =
                    new MemberUpdateRequest("new-nickname", phoneNumber, "성북구", true);

            MemberResponse response = memberService.update(member.getId(), request);

            assertThat(response.phoneNumber()).isEqualTo(phoneNumber);
        }
    }
}
