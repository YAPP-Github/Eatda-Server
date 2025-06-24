package timeeat.controller.web.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtManagerTest {

    private final String secretKey = "secretKey".repeat(32);
    private final JwtManager jwtManager = new JwtManager(
            new JwtProperties(secretKey, Duration.ofHours(1), Duration.ofDays(14)));

    @Nested
    class IssueAccessToken {

        @Test
        void 액세스_토큰을_발행할_수_있다() {
            long id = 12345L;

            assertThatCode(() -> jwtManager.issueAccessToken(id))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class IssueRefreshToken {

        @Test
        void 액세스_토큰을_발행할_수_있다() {
            long id = 12345L;

            assertThatCode(() -> jwtManager.issueRefreshToken(id))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class ResolveAccessToken {

        @Test
        void 액세스_토큰을_해석할_수_있다() {
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            long actualId = jwtManager.resolveAccessToken(accessToken);

            assertThat(actualId).isEqualTo(id);
        }

        @Test
        void 만료된_액세스_토큰을_해석하면_에러가_발생한다() {
            Duration accessTokenExpiration = Duration.ofNanos(1);
            JwtManager jwtManager = new JwtManager(
                    new JwtProperties(secretKey, accessTokenExpiration, Duration.ofDays(14)));
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            assertThatThrownBy(() -> jwtManager.resolveAccessToken(accessToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("이미 만료된 토큰입니다.");
        }

        @Test
        void 유효하지_않은_액세스_토큰을_해석하면_에러가_발생한다() {
            String accessToken = "aaa.bbb.ccc";

            assertThatThrownBy(() -> jwtManager.resolveAccessToken(accessToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("인증되지 않은 회원입니다.");
        }

        @Test
        void 액세스_토큰의_타입이_다르면_에러가_발생한다() {
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            assertThatThrownBy(() -> jwtManager.resolveAccessToken(refreshToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("인증되지 않은 회원입니다.");
        }
    }

    @Nested
    class ResolveRefreshToken {

        @Test
        void 리프레시_토큰을_해석할_수_있다() {
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            long actualId = jwtManager.resolveRefreshToken(refreshToken);

            assertThat(actualId).isEqualTo(id);
        }

        @Test
        void 만료된_리프레시_토큰을_해석하면_에러가_발생한다() {
            Duration refreshTokenExpiration = Duration.ofNanos(1);
            JwtManager jwtManager = new JwtManager(
                    new JwtProperties(secretKey, Duration.ofHours(1), refreshTokenExpiration));
            long id = 12345L;
            String refreshToken = jwtManager.issueRefreshToken(id);

            assertThatThrownBy(() -> jwtManager.resolveRefreshToken(refreshToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("이미 만료된 토큰입니다.");
        }

        @Test
        void 유효하지_않은_리프레시_토큰을_해석하면_에러가_발생한다() {
            String refreshToken = "aaa.bbb.ccc";

            assertThatThrownBy(() -> jwtManager.resolveRefreshToken(refreshToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("인증되지 않은 회원입니다.");
        }

        @Test
        void 리프레시_토큰의_타입이_다르면_에러가_발생한다() {
            long id = 12345L;
            String accessToken = jwtManager.issueAccessToken(id);

            assertThatThrownBy(() -> jwtManager.resolveRefreshToken(accessToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("인증되지 않은 회원입니다.");
        }
    }
}
