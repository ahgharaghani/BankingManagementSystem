package exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String accountId, BigDecimal balance, BigDecimal required) {
        super(String.format("Account %s has insufficient funds. Balance: %.2f, Required: %.2f",
                accountId, balance, required));
    }
}