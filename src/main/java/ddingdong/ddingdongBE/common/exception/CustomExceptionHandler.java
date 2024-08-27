package ddingdong.ddingdongBE.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Hidden
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CustomExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResponse handleSystemException(Throwable exception, HttpServletRequest request) {
        String connectionInfo = createLogConnectionInfo(request);

        loggingApplicationError(connectionInfo
                + "\n"
                + "[SYSTEM-ERROR]" + " : " + exception.getMessage());

        return new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "Internal Sever Error", LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception,
                                                        HttpServletRequest request) {
        String connectionInfo = createLogConnectionInfo(request);

        loggingApplicationError(connectionInfo
                + "\n"
                + exception.getClass().getSimpleName() + " : " + exception.getMessage());

        return new ErrorResponse(BAD_REQUEST.value(), exception.getMessage(), LocalDateTime.now()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException exception, HttpServletRequest request) {
        String connectionInfo = createLogConnectionInfo(request);

        loggingApplicationError(connectionInfo
                + "\n"
                + BAD_REQUEST.value() + " : " + exception.message);

        return new ErrorResponse(exception.errorCode, exception.message, LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        String connectionInfo = createLogConnectionInfo(request);

        loggingApplicationError(connectionInfo
                + "\n"
                + exception.getClass().getSimpleName() + " : " + exception.message);

        return new ErrorResponse(exception.errorCode, exception.message, LocalDateTime.now()
        );
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                               HttpServletRequest request) {
        String connectionInfo = createLogConnectionInfo(request);

        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("입력된 값이 올바르지 않습니다.");

        loggingApplicationError(connectionInfo
                + "\n"
                + exception.getClass().getSimpleName() + " : " + message);

        return new ErrorResponse(BAD_REQUEST.value(), exception.getMessage(), LocalDateTime.now()
        );
    }


    // TODO : NoSuchElementException 대신 PersistenceException.ResourceNotFound()로 전환 필요
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleNoSuchElementException(NoSuchElementException e) {
        return ExceptionResponse.of(BAD_REQUEST, e.getMessage());
    }

    // TODO : presigned url 도입 시, 삭제 예정
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(NOT_FOUND)
    public ExceptionResponse handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ExceptionResponse.of(BAD_REQUEST, e.getMessage());
    }


    private String createLogConnectionInfo(HttpServletRequest request) {
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For")
                : request.getRemoteAddr();

        return requestMethod + requestUrl + "?" + queryString + " from ip: " + clientIp;
    }

    private void loggingApplicationError(String applicationLog) {
        log.warn("errorLog = {}", applicationLog);
    }
}