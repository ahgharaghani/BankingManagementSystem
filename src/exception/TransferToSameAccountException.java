package exception;

public class TransferToSameAccountException extends Exception {
    public TransferToSameAccountException(String accountId) {
        super("Cannot transfer to the same account: " + accountId);
    }
}