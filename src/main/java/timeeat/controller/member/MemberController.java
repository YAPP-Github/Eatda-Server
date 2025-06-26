package timeeat.controller.member;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import timeeat.controller.web.jwt.JwtManager;
import timeeat.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final JwtManager jwtManager;
    private final MemberService memberService;

    @GetMapping("/api/member/login/auth")
    public ResponseEntity<Void> redirectOauthLoginPage() {
        URI oauthLoginUrl = memberService.getOauthLoginUrl();

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(oauthLoginUrl)
                .build();
    }

    @PostMapping("/api/member/login")
    public ResponseEntity<TokenResponse> login(@RequestBody MemberLoginRequest request) {
        memberService.login(request);

        TokenResponse response = new TokenResponse(
                jwtManager.issueAccessToken(1L),
                jwtManager.issueRefreshToken(1L));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/api/member/reissue")
    public ResponseEntity<TokenResponse> reissueToken(@RequestBody ReissueRequest request) {
        long id = jwtManager.resolveRefreshToken(request.refreshToken());

        TokenResponse response = new TokenResponse(
                jwtManager.issueAccessToken(id),
                jwtManager.issueRefreshToken(id));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
