package exception;

/** Thrown when saving or loading system state fails. */
public class PersistenceException extends Exception {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    public PersistenceException(String message) {
        super(message);
    }
}