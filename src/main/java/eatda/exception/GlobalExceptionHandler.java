package eatda.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(BindException exception) {
        return toErrorResponse(EtcErrorCode.CLIENT_REQUEST_ERROR, exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        return toErrorResponse(EtcErrorCode.CLIENT_REQUEST_ERROR, exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        return toErrorResponse(EtcErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH, exception);
    }

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<ErrorResponse> handleClientAbortException(ClientAbortException exception) {
        log.warn("[ClientAbortException] {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return toErrorResponse(EtcErrorCode.ALREADY_DISCONNECTED, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ) {
        return toErrorResponse(EtcErrorCode.METHOD_NOT_SUPPORTED, exception);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception
    ) {
        return toErrorResponse(EtcErrorCode.MEDIA_TYPE_NOT_SUPPORTED, exception);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        return toErrorResponse(EtcErrorCode.NO_RESOURCE_FOUND, exception);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException exception) {
        return toErrorResponse(EtcErrorCode.NO_COOKIE_FOUND, exception);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        return toErrorResponse(EtcErrorCode.NO_HEADER_FOUND, exception);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        return toErrorResponse(EtcErrorCode.NO_PARAMETER_FOUND, exception);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception) {
        return toErrorResponse(EtcErrorCode.VALIDATION_ERROR, exception);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        log.warn("[BusinessException] Code: {}, Message: {}", exception.getErrorCode(), exception.getMessage());
        ErrorResponse response = new ErrorResponse(exception.getErrorCode());
        return ResponseEntity.status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("[Unhandled Exception] {}: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        return toErrorResponse(EtcErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    private ResponseEntity<ErrorResponse> toErrorResponse(EtcErrorCode errorCode, Exception exception) {
        if (exception != null) {
            if (errorCode.getStatus().is4xxClientError()) {
                log.warn("[Client Error] {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
            } else {
                log.error("[Server Error] {}: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
            }
        }
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }
}
