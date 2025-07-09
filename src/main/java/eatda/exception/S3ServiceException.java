package eatda.exception;

import lombok.Getter;

@Getter
public class S3ServiceException extends RuntimeException {
    private final EtcErrorCode errorCode;

    public S3ServiceException(EtcErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
