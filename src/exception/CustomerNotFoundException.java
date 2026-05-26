package exception;

public class CustomerNotFoundException extends Exception {
  public CustomerNotFoundException(String nationalId) {
    super("Customer not found with national ID: " + nationalId);
  }
}