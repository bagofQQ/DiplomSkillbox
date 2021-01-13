package main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PostsException.class)
    protected ResponseEntity<?> handlerPostException(){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
