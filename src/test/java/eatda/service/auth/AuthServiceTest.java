package eatda.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.client.oauth.OauthMemberInformation;
import eatda.controller.member.MemberResponse;
import eatda.service.BaseServiceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthServiceTest extends BaseServiceTest {

    @Nested
    class Login {

        @Test
        void 로그인_최초_요청_시_회원가입_및_로그인_처리를_한다() {
            OauthMemberInformation oauthInformation = new OauthMemberInformation(123L, "abc@kakao.com", "nickname");

            MemberResponse response = authService.login(oauthInformation);

            assertAll(
                    () -> assertThat(response.isSignUp()).isTrue(),
                    () -> assertThat(response.id()).isNotZero(),
                    () -> assertThat(response.email()).isEqualTo("abc@kakao.com"),
                    () -> assertThat(response.nickname()).isEqualTo("nickname")
            );
        }

        @Test
        void 로그인_최초_요청이_아닐_경우_로그인만_처리를_한다() {
            memberGenerator.generate("123");
            OauthMemberInformation oauthInformation = new OauthMemberInformation(123L, "abc@kakao.com", "nickname");

            MemberResponse response = authService.login(oauthInformation);

            assertThat(response.isSignUp()).isFalse();
        }
    }
}
