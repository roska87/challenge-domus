package domus.challenge.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public Mono<ProblemDetail> handleBadRequest(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid request");
        pd.setDetail(safeMessage(ex));
        return Mono.just(pd);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGeneric(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("An unexpected error occurred.");
        return Mono.just(pd);
    }

    private String safeMessage(Exception ex) {
        if (ex instanceof MethodArgumentTypeMismatchException matme) {
            return "Parameter '" + matme.getName() + "' must be a number.";
        }
        if (ex instanceof MissingRequestValueException mrve) {
            return "Missing required parameter: " + mrve.getReason();
        }
        return ex.getMessage();
    }

}
