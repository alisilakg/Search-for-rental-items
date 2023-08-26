package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("Получен стату 500 INTERNAL SERVER ERROR {}", e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.debug("Получен стату 400 BAD REQUEST {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidationException(final MethodArgumentNotValidException e) {
        log.debug("Получен стату 400 BAD REQUEST {}", e.getMessage(), e);
        return new ErrorResponse("Аргумент не прошел валидацию!");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> errorHandler(IllegalArgumentException e) {
        log.debug("Получен стату 500 INTERNAL SERVER ERROR {}", e.getMessage(), e);
        Map<String, String> resp = new HashMap<>();
        resp.put("error", String.format("Unknown state: UNSUPPORTED_STATUS"));
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Получен стату 400 BAD REQUEST {}", e.getMessage(), e);
        return new ErrorResponse("Аргумент не прошел валидацию!");
    }

}
