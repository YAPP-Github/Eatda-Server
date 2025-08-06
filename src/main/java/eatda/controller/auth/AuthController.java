package eatda.controller.auth;

import eatda.client.oauth.OauthMemberInformation;
import eatda.controller.member.MemberResponse;
import eatda.controller.web.jwt.JwtManager;
import eatda.service.auth.AuthService;
import eatda.service.auth.OauthService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OauthService oauthService;
    private final JwtManager jwtManager;

    @GetMapping("/api/auth/login/oauth")
    public ResponseEntity<Void> redirectOauthLoginPage(@RequestHeader(HttpHeaders.REFERER) String origin) {
        URI oauthLoginUrl = oauthService.getOauthLoginUrl(origin);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(oauthLoginUrl)
                .build();
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        OauthMemberInformation oauthInformation = oauthService.getOAuthInformation(request.code(), request.origin());
        MemberResponse member = authService.login(oauthInformation);

        TokenResponse token = new TokenResponse(
                jwtManager.issueAccessToken(member.id()),
                jwtManager.issueRefreshToken(member.id()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new LoginResponse(token, member));
    }

    @PostMapping("/api/auth/reissue")
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
