package exception;

import java.math.BigDecimal;

/** Thrown when a monetary amount is null, zero, or negative. */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount + ". Amount must be greater than zero.");
    }
}