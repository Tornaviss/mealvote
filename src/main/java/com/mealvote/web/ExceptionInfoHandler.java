package com.mealvote.web;

import com.mealvote.util.ValidationUtil;
import com.mealvote.util.exception.ExceptionInfo;
import com.mealvote.util.exception.IllegalOperationException;
import com.mealvote.util.exception.IllegalRequestDataException;
import com.mealvote.util.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private static final Map<String, String> CONSTRAINS_MAP = Map.of(
            "users_unique_email_idx", "user with this email already exists",
            "dish_name_menu_unique_idx", "dish with this name already contains in the menu",
            "constraint_index_b", "restaurant with this name is already exists"
    );

    @ResponseStatus(value = HttpStatus.CONFLICT)  // 409
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ExceptionInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();

        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            Optional<Map.Entry<String, String>> entry = CONSTRAINS_MAP.entrySet().stream()
                    .filter(it -> lowerCaseMsg.contains(it.getKey()))
                    .findAny();
            if (entry.isPresent()) {
                return logAndGetErrorInfo(req, e, false, entry.get().getValue());
            }
        }
        return logAndGetErrorInfo(req, e, true);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalOperationException.class)
    public ExceptionInfo illegalOperationError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ExceptionInfo bindValidationError(HttpServletRequest req, Exception e) {
        BindingResult result = e instanceof BindException ?
                ((BindException) e).getBindingResult() : ((MethodArgumentNotValidException) e).getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return logAndGetErrorInfo(req, e, false,
                fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toArray(String[]::new)
        );
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ExceptionInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionInfo entityNotFound(HttpServletRequest req, Exception e) {
        String message = "Not found entity with " + e.getMessage();
        return logAndGetErrorInfo(req, e, true, message);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ExceptionInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true);
    }

    private ExceptionInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, String... details) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logException);
        return new ExceptionInfo(e.getClass().getSimpleName(),
                req.getRequestURL(),
                details.length != 0 ? details : new String[]{ValidationUtil.getMessage(rootCause)});
    }

}