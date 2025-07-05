package timeeat.controller.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import timeeat.controller.web.auth.LoginMember;
import timeeat.service.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/member/nickname/check")
    public ResponseEntity<Void> checkNickname(LoginMember member, @RequestParam String nickname) {
        memberService.validateNickname(nickname, member.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/member/phone-number/check")
    public ResponseEntity<Void> checkPhoneNumber(LoginMember member, @RequestParam String phoneNumber) {
        memberService.validatePhoneNumber(phoneNumber, member.id());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/member")
    public ResponseEntity<MemberResponse> updateMember(LoginMember member,
                                                       @RequestBody @Valid MemberUpdateRequest request) {
        MemberResponse response = memberService.update(member.id(), request);
        return ResponseEntity.ok(response);
    }
}
