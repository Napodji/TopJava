package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private static final String EMAIL_DUPLICATE_CONSTRAINT = "users_unique_email_idx";
    private static final String MEAL_DATETIME_DUPLICATE_CONSTRAINT = "meal_unique_user_datetime_idx";

    private final MessageSource messageSource;

    public ExceptionInfoHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT) // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMessage = ValidationUtil.getRootCause(e).getMessage();
        String lowerMessage = rootMessage == null ? "" : rootMessage.toLowerCase();
        String messageCode;
        if (lowerMessage.contains(EMAIL_DUPLICATE_CONSTRAINT)) {
            messageCode = "error.duplicateEmail";
        } else if (lowerMessage.contains(MEAL_DATETIME_DUPLICATE_CONSTRAINT)) {
            messageCode = "error.duplicateMealDateTime";
        } else {
            return logAndGetErrorInfo(req, e, true, DATA_ERROR);
        }
        String detail = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
        log.warn("{} at request {}: {}", DATA_ERROR, req.getRequestURL(), detail);
        return new ErrorInfo(req.getRequestURL(), DATA_ERROR, detail);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo validationError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    @ExceptionHandler(BindException.class)
    public ErrorInfo bindValidationError(HttpServletRequest req, BindException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, ValidationUtil.getErrorResponse(e.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo internalError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType, String... details) {
        if (details.length == 0) {
            details = new String[]{ValidationUtil.getRootCause(e).getMessage()};
        }
        if (logStackTrace) {
            log.error(errorType + " at request " + req.getRequestURL(), ValidationUtil.getRootCause(e));
        } else {
            log.warn("{} at request {}: {}", errorType, req.getRequestURL(), Arrays.toString(details));
        }
        return new ErrorInfo(req.getRequestURL(), errorType, details);
    }
}