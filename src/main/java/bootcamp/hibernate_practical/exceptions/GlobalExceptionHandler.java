package bootcamp.hibernate_practical.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;


// handle exceptions from all controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle the error when a book is not found
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(
            BookNotFoundException exception) {
        return ResponseEntity
                // set the HTTP status to 404
                .status(HttpStatus.NOT_FOUND)
                // return the error message as JSON
                .body(Map.of("message", exception.getMessage()));
    }
}
