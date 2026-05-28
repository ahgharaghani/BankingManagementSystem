package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents a bank customer */
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nationalId;
    private String firstName;
    private String lastName;
    private final List<String> accountIds;

    public Customer(String nationalId, String firstName, String lastName) {
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountIds = new ArrayList<>();
    }

    public String getNationalId() { return nationalId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }

    public List<String> getAccountIds() {
        return Collections.unmodifiableList(accountIds);
    }

    public void addAccountId(String accountId) {
        if (!accountIds.contains(accountId)) {
            accountIds.add(accountId);
        }
    }

    public void removeAccountId(String accountId) {
        accountIds.remove(accountId);
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%s, name=%s %s, accounts=%d]",
                nationalId, firstName, lastName, accountIds.size());
    }
}