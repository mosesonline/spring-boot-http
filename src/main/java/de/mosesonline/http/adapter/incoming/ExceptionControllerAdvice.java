package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.model.exception.BackendException;
import de.mosesonline.http.model.exception.BackendUnknownException;
import de.mosesonline.http.model.exception.WebError;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({BackendException.class})
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<WebError> handleException(BackendException e) {
        if (e.getCause() instanceof ExecutionException exception) {
            return handleException(exception);
        } else if (e.getCause() instanceof Exception exception) {
            return handleException(exception);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new WebError(e.getCause().getMessage()));
        }
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<WebError> handleException(TimeoutException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT.value()).body(new WebError(e.getMessage()));
    }

    @ExceptionHandler({ExecutionException.class})
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<WebError> handleException(ExecutionException e) {
        if (e.getCause() instanceof TimeoutException timeoutException) {
            return handleException(timeoutException);
        } else if(e.getCause() instanceof Exception exception){
            return handleException(exception);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new WebError(e.getMessage()));
        }
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<WebError> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new WebError(e.getMessage()));
    }

    @ExceptionHandler(value = BeanCreationException.class)
    public ResponseEntity<WebError> handleException(BeanCreationException e) {
        if (e.getRootCause() instanceof BackendUnknownException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(new WebError(exception.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new WebError("Cannot instantiate bean " + e.getBeanName()));
        }
    }
}
