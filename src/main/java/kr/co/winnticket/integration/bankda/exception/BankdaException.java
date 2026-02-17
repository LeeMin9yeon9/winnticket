package kr.co.winnticket.integration.bankda.exception;

public class BankdaException extends RuntimeException {

    private final int status;

    public BankdaException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
