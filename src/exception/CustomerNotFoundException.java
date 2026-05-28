package exception;

/** Thrown when a customer with the given national ID does not exist. */
public class CustomerNotFoundException extends Exception {
  public CustomerNotFoundException(String nationalId) {
    super("Customer not found with national ID: " + nationalId);
  }
}