package timeeat.exception;

public record ErrorResponse(String errorCode, String message) {

    public ErrorResponse(BusinessErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

    public ErrorResponse(EtcErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
