package repository;

import model.Account;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repository for Accounts */
public class AccountRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConcurrentHashMap<String, Account> store = new ConcurrentHashMap<>();

    public boolean existsById(String accountId) {
        return store.containsKey(accountId);
    }

    public void save(Account account) {
        store.put(account.getAccountId(), account);
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(store.get(accountId));
    }

    public Collection<Account> findAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    public boolean delete(String accountId) {
        return store.remove(accountId) != null;
    }

    public int count() {
        return store.size();
    }
}