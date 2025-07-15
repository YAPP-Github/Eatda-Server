package eatda.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {

    // Member
    INVALID_MOBILE_PHONE_NUMBER("MEM001", "전화번호는 11자리여야 합니다."),
    INVALID_INTEREST_AREA("MEM002", "유효하지 않은 관심 지역입니다."),
    INVALID_MARKETING_CONSENT("MEM003", "마케팅 동의 여부는 필수입니다."),
    INVALID_MEMBER_ID("MEM004", "유효하지 않은 회원 ID입니다."),
    INVALID_SOCIAL_ID("MEM005", "소셜 ID는 필수입니다."),
    DUPLICATE_NICKNAME("MEM006", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_PHONE_NUMBER("MEM007", "이미 사용 중인 전화번호입니다."),
    INVALID_EMAIL("MEM008", "유효하지 않은 이메일 형식입니다."),

    // Store
    INVALID_STORE_CATEGORY("STO001", "유효하지 않은 매장 카테고리입니다."),
    INVALID_STORE_COORDINATES_NULL("STO007", "좌표 값은 필수입니다."),
    OUT_OF_SEOUL_LATITUDE_RANGE("STO010", "서비스 지역(서울)을 벗어난 위도 값입니다."),
    OUT_OF_SEOUL_LONGITUDE_RANGE("STO011", "서비스 지역(서울)을 벗어난 경도 값입니다."),

    // Cheer
    INVALID_CHEER_DESCRIPTION("CHE001", "응원 메시지는 필수입니다."),
    INVALID_CHEER_IMAGE_KEY("CHE002", "응원 이미지 키가 비어 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Map
    MAP_SERVER_ERROR("MAP001", "지도 서버와의 통신 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Auth
    UNAUTHORIZED_MEMBER("AUTH001", "인증되지 않은 회원입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH002", "이미 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ORIGIN("AUTH003", "허용되지 않은 오리진입니다."),
    OAUTH_SERVER_ERROR("AUTH003", "OAuth 서버와의 통신 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // image
    INVALID_IMAGE_TYPE("CLIENT010", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("SERVER002", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_URL_GENERATION_FAILED("SERVER003", "파일 URL 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PRESIGNED_URL_GENERATION_FAILED("SERVER004", "Presigned URL 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    BusinessErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    BusinessErrorCode(String code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }
}
