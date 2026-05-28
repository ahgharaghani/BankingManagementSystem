package model;

import exception.PersistenceException;
import observer.ConsoleTransactionObserver;
import observer.TransactionEventPublisher;
import persistence.BankSnapshot;
import persistence.PersistenceService;
import repository.AccountRepository;
import repository.CustomerRepository;
import service.AccountService;
import service.CustomerService;
import util.AccountIdGenerator;
import util.BankLogger;

import java.util.logging.Logger;

/** Central core for the banking system that handles all components */
public class Bank {

    private static final Logger log = BankLogger.getLogger();

    // === Singleton =========================================================

    private static volatile Bank instance;

    public static Bank getInstance() {
        if (instance == null) {
            synchronized (Bank.class) {
                if (instance == null) {
                    instance = new Bank();
                }
            }
        }
        return instance;
    }

    // === Components =========================================================

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventPublisher eventPublisher;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final PersistenceService persistenceService;

    private Bank() {
        customerRepository = new CustomerRepository();
        accountRepository  = new AccountRepository();
        eventPublisher = new TransactionEventPublisher();

        eventPublisher.addObserver(new ConsoleTransactionObserver());

        customerService = new CustomerService(customerRepository);
        accountService = new AccountService(accountRepository, customerRepository, eventPublisher);
        persistenceService = new PersistenceService();
    }

    /** saves the bank snapshot to disk */
    public void save() throws PersistenceException {
        BankSnapshot snapshot = new BankSnapshot(
                customerRepository.findAll(),
                accountRepository.findAll(),
                AccountIdGenerator.current()
        );
        persistenceService.save(snapshot);
        log.info("model.Bank state persisted.");
    }

    /** loads a saved snapshot from disk */
    public void load() throws PersistenceException {
        BankSnapshot snapshot = persistenceService.load();
        if (snapshot == null) return;

        snapshot.getCustomers().forEach(customerRepository::save);
        snapshot.getAccounts().forEach(accountRepository::save);
        AccountIdGenerator.reset(snapshot.getAccountIdCounterValue());
        log.info("model.Bank state restored from disk.");
    }

    // === Getters =========================================================

    public CustomerService getCustomerService() { return customerService; }
    public AccountService getAccountService() { return accountService; }
    public TransactionEventPublisher getEventPublisher() { return eventPublisher; }
}