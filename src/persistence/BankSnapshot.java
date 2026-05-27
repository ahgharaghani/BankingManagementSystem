package persistence;

import model.Account;
import model.Customer;

import java.io.Serializable;
import java.util.Collection;

public class BankSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Collection<Customer> customers;
    private final Collection<Account> accounts;
    private final long accountIdCounterValue;

    public BankSnapshot(Collection<Customer> customers,
                        Collection<Account> accounts,
                        long accountIdCounterValue) {
        this.customers = customers;
        this.accounts = accounts;
        this.accountIdCounterValue = accountIdCounterValue;
    }

    public Collection<Customer> getCustomers() { return customers; }
    public Collection<Account> getAccounts() { return accounts; }
    public long getAccountIdCounterValue() { return accountIdCounterValue; }
}