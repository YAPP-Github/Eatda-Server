package timeeat.controller.member;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import timeeat.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/login/auth")
    public ResponseEntity<Void> redirectOauthLoginPage() {
        URI oauthLoginUrl = memberService.getOauthLoginUrl();

        return ResponseEntity
                .status(302)
                .location(oauthLoginUrl)
                .build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequest request) {
        memberService.login(request);
        return ResponseEntity.ok("Oauth 로그인 성공"); // TODO 회원 생성 후 정보 반환
    }
}
