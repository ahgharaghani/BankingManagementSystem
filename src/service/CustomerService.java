package service;

import exception.CustomerNotFoundException;
import exception.DuplicateCustomerException;
import model.Customer;
import repository.CustomerRepository;

import java.util.Collection;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(String nationalId, String firstName, String lastName)
            throws DuplicateCustomerException {
        if (customerRepository.existsByNationalId(nationalId)) {
            throw new DuplicateCustomerException(nationalId);
        }
        Customer customer = new Customer(nationalId, firstName, lastName);
        customerRepository.save(customer);
        return customer;
    }

    public Customer getCustomer(String nationalId) throws CustomerNotFoundException {
        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new CustomerNotFoundException(nationalId));
    }

    public Collection<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}