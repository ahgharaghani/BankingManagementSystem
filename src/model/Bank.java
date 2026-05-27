package model;

import observer.ConsoleTransactionObserver;
import observer.TransactionEventPublisher;
import repository.AccountRepository;
import repository.CustomerRepository;
import service.AccountService;
import service.CustomerService;
import util.AccountIdGenerator;
import util.BankLogger;

import java.util.logging.Logger;

public class Bank {

    private static final Logger log = BankLogger.getLogger();

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

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventPublisher eventPublisher;
    private final CustomerService customerService;
    private final AccountService accountService;

    private Bank() {
        customerRepository = new CustomerRepository();
        accountRepository  = new AccountRepository();
        eventPublisher = new TransactionEventPublisher();

        eventPublisher.addObserver(new ConsoleTransactionObserver());

        customerService = new CustomerService(customerRepository);
        accountService = new AccountService(accountRepository, customerRepository, eventPublisher);
    }


    public CustomerService getCustomerService() { return customerService; }
    public AccountService getAccountService() { return accountService; }
    public TransactionEventPublisher getEventPublisher() { return eventPublisher; }
}