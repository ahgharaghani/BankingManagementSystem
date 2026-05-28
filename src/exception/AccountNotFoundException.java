package exception;

/** Thrown when an account with the given ID does not exist in the system. */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}