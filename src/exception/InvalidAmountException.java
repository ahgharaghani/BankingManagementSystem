package exception;

import java.math.BigDecimal;

public class InvalidAmountException extends Exception {
    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount + ". Amount must be greater than zero.");
    }
}