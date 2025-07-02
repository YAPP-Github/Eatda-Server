package timeeat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EtcErrorCode {

    CLIENT_REQUEST_ERROR("CLIENT001", "클라이언트 요청이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_TYPE_MISMATCH("CLIENT002", "요청 타입이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_DISCONNECTED("CLIENT003", "이미 클라이언트에서 요청이 종료되었습니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED("CLIENT004", "허용되지 않은 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    MEDIA_TYPE_NOT_SUPPORTED("CLIENT005", "허용되지 않은 미디어 타입입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    NO_RESOURCE_FOUND("CLIENT006", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NO_COOKIE_FOUND("CLIENT007", "필수 쿠키 값이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_HEADER_FOUND("CLIENT007", "필수 헤더 값이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("SERVER001", "서버 내부 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    EtcErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
