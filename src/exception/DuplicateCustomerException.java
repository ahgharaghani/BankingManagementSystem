package exception;

/** Thrown when a customer with the same national ID already exists. */
public class DuplicateCustomerException extends Exception {
    public DuplicateCustomerException(String nationalId) {
        super("Customer with national ID already exists: " + nationalId);
    }
}