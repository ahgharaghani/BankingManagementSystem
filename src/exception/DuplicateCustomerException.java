package exception;

public class DuplicateCustomerException extends Exception {
    public DuplicateCustomerException(String nationalId) {
        super("Customer with national ID already exists: " + nationalId);
    }
}