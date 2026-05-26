package repository;

import model.Customer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConcurrentHashMap<String, Customer> store = new ConcurrentHashMap<>();

    public boolean existsByNationalId(String nationalId) {
        return store.containsKey(nationalId);
    }

    public void save(Customer customer) {
        store.put(customer.getNationalId(), customer);
    }

    public Optional<Customer> findByNationalId(String nationalId) {
        return Optional.ofNullable(store.get(nationalId));
    }

    public Collection<Customer> findAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    public boolean delete(String nationalId) {
        return store.remove(nationalId) != null;
    }

    public int count() {
        return store.size();
    }
}