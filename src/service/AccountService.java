package service;

import exception.*;
import model.*;
import model.enums.TransactionStatus;
import model.enums.TransactionType;
import observer.TransactionEventPublisher;
import repository.AccountRepository;
import repository.CustomerRepository;
import util.AccountIdGenerator;
import util.BankLogger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/** Responsible for all account-related operations */
public class AccountService {

    private static final Logger log = BankLogger.getLogger();

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionEventPublisher eventPublisher;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    // === Account opening =========================================================

    /** opens a saving account */
    public SavingsAccount openSavingsAccount(String nationalId,
                                             BigDecimal initialBalance,
                                             BigDecimal interestRate)
            throws CustomerNotFoundException {
        Customer customer = findCustomer(nationalId);
        String accountId = AccountIdGenerator.next();
        SavingsAccount account = new SavingsAccount(accountId, nationalId, initialBalance, interestRate);
        accountRepository.save(account);
        customer.addAccountId(accountId);
        log.info("Opened SavingsAccount " + accountId + " for customer " + nationalId);
        return account;
    }

    /** opens a checking account */
    public CheckingAccount openCheckingAccount(String nationalId,
                                               BigDecimal initialBalance,
                                               BigDecimal feeRate)
            throws CustomerNotFoundException {
        Customer customer = findCustomer(nationalId);
        String accountId = AccountIdGenerator.next();
        CheckingAccount account = new CheckingAccount(accountId, nationalId, initialBalance, feeRate);
        accountRepository.save(account);
        customer.addAccountId(accountId);
        log.info("Opened CheckingAccount " + accountId + " for customer " + nationalId);
        return account;
    }

    // === Financial operations =========================================================

    /** deposit money into the given account */
    public Transaction deposit(String accountId, BigDecimal amount)
            throws AccountNotFoundException, InvalidAmountException {
        Account account = findAccount(accountId);
        Transaction tx = account.deposit(amount);
        log.info("DEPOSIT " + tx);
        eventPublisher.publish(tx);
        return tx;
    }

    /** withdraws money from the given account */
    public Transaction withdraw(String accountId, BigDecimal amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account account = findAccount(accountId);
        Transaction tx = account.withdraw(amount);
        log.info("WITHDRAWAL " + tx);
        eventPublisher.publish(tx);
        return tx;
    }

    /** transfers money between two accounts */
    public Transaction transfer(String sourceId, String targetId, BigDecimal amount)
            throws AccountNotFoundException, TransferToSameAccountException,
            InvalidAmountException, InsufficientFundsException {

        if (sourceId.equals(targetId)) {
            throw new TransferToSameAccountException(sourceId);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account source = findAccount(sourceId);
        Account target = findAccount(targetId);

        // lock the smaller ID first
        Account first  = sourceId.compareTo(targetId) < 0 ? source : target;
        Account second = sourceId.compareTo(targetId) < 0 ? target : source;

        ReentrantLock lock1 = first.getLock();
        ReentrantLock lock2 = second.getLock();

        lock1.lock();
        try {
            lock2.lock();
            try {
                // Check if source has enough balance
                BigDecimal sourceFee = source instanceof CheckingAccount
                        ? ((CheckingAccount) source).calculateWithdrawalFee(amount)
                        : BigDecimal.ZERO;
                BigDecimal totalDebit = amount.add(sourceFee);

                if (source.getBalance().compareTo(totalDebit) < 0) {
                    Transaction failed = Transaction.builder()
                            .type(TransactionType.TRANSFER_OUT)
                            .amount(amount)
                            .sourceAccountId(sourceId)
                            .targetAccountId(targetId)
                            .status(TransactionStatus.FAILED)
                            .description("Insufficient funds for transfer")
                            .build();
                    source.recordTransaction(failed);
                    log.warning("TRANSFER FAILED (insufficient funds): " + failed);
                    eventPublisher.publish(failed);
                    throw new InsufficientFundsException(sourceId, source.getBalance(), totalDebit);
                }

                // applying to both accounts
                source.adjustBalance(totalDebit.negate());
                target.adjustBalance(amount);

                Transaction outTx = Transaction.builder()
                        .type(TransactionType.TRANSFER_OUT)
                        .amount(amount)
                        .sourceAccountId(sourceId)
                        .targetAccountId(targetId)
                        .status(TransactionStatus.SUCCESS)
                        .description("Transfer to " + targetId)
                        .build();

                Transaction inTx = Transaction.builder()
                        .type(TransactionType.TRANSFER_IN)
                        .amount(amount)
                        .sourceAccountId(sourceId)
                        .targetAccountId(targetId)
                        .status(TransactionStatus.SUCCESS)
                        .description("Transfer from " + sourceId)
                        .build();

                source.recordTransaction(outTx);
                target.recordTransaction(inTx);

                log.info("TRANSFER " + outTx);
                eventPublisher.publish(outTx);
                eventPublisher.publish(inTx);
                return outTx;

            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }
    }

    // === Getters =========================================================

    public Account getAccount(String accountId) throws AccountNotFoundException {
        return findAccount(accountId);
    }

    public Collection<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // === Helpers =========================================================

    private Account findAccount(String accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private Customer findCustomer(String nationalId) throws CustomerNotFoundException {
        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new CustomerNotFoundException(nationalId));
    }
}