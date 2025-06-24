package timeeat.controller.web.auth;

import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import timeeat.controller.web.jwt.JwtManager;

@AllArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtManager jwtManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String accessToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken == null) {
            // TODO : BusinessException 으로 변경 - UNAUTHORIZED_MEMBER("AUTH001", "인증되지 않은 회원입니다.");
            throw new RuntimeException("인증되지 않은 회원입니다.");
        }
        long memberId = jwtManager.resolveAccessToken(accessToken);
        return new LoginMember(memberId);
    }
}
