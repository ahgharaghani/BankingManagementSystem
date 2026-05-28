package exception;

/** Thrown when a transfer is attempted between the same source and destination account. */
public class TransferToSameAccountException extends Exception {
    public TransferToSameAccountException(String accountId) {
        super("Cannot transfer to the same account: " + accountId);
    }
}