package timeeat.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(BindException exception) {
        return toErrorResponse(EtcErrorCode.CLIENT_REQUEST_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        return toErrorResponse(EtcErrorCode.CLIENT_REQUEST_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        return toErrorResponse(EtcErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH);
    }

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<ErrorResponse> handleClientAbortException(ClientAbortException exception) {
        return toErrorResponse(EtcErrorCode.ALREADY_DISCONNECTED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ) {
        return toErrorResponse(EtcErrorCode.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception
    ) {
        return toErrorResponse(EtcErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        return toErrorResponse(EtcErrorCode.NO_RESOURCE_FOUND);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException exception) {
        return toErrorResponse(EtcErrorCode.NO_COOKIE_FOUND);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        return toErrorResponse(EtcErrorCode.NO_HEADER_FOUND);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        return toErrorResponse(EtcErrorCode.NO_PARAMETER_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorResponse response = new ErrorResponse(exception.getErrorCode());
        return ResponseEntity.status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return toErrorResponse(EtcErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toErrorResponse(EtcErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }
}
