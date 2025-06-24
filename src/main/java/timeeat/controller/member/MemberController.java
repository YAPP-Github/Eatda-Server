package timeeat.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import timeeat.controller.web.jwt.JwtManager;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final JwtManager jwtManager;

    @PostMapping("/api/login")
    public ResponseEntity<TokenResponse> login() {
        // TODO : memberService.login() 메서드를 호출하여 로그인 처리

        TokenResponse response = new TokenResponse(
                jwtManager.issueAccessToken(1L),
                jwtManager.issueRefreshToken(1L));
        return ResponseEntity.ok(response);
    }
}
