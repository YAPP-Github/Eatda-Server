package eatda.controller.auth;

import eatda.controller.member.MemberResponse;

public record LoginResponse(TokenResponse token, MemberResponse information) {

}
