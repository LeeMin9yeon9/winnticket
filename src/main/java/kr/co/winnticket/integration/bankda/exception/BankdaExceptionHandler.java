package kr.co.winnticket.integration.bankda.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "kr.co.winnticket.integration.bankda")
public class BankdaExceptionHandler {

    @ExceptionHandler(BankdaException.class)
    public ResponseEntity<String> handleBankdaException(BankdaException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getMessage());
    }
}
