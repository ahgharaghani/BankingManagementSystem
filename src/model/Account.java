package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.enums.TransactionStatus;
import model.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String accountId;
    private BigDecimal balance;
    private final String ownerId;
    private final List<Transaction> transactionHistory;

    private transient ReentrantLock lock;

    protected Account(String accountId, String ownerId, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.ownerId = ownerId;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.lock = new ReentrantLock(true); // fair lock
    }

    public void initTransientFields() {
        this.lock = new ReentrantLock(true);
    }

    public Transaction deposit(BigDecimal amount) throws InvalidAmountException {
        validatePositiveAmount(amount);
        lock.lock();
        try {
            balance = balance.add(amount);
            Transaction tx = Transaction.builder()
                    .type(TransactionType.DEPOSIT)
                    .amount(amount)
                    .targetAccountId(accountId)
                    .status(TransactionStatus.SUCCESS)
                    .description("Deposit to account " + accountId)
                    .build();
            transactionHistory.add(tx);
            return tx;
        } finally {
            lock.unlock();
        }
    }

    public Transaction withdraw(BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException {
        validatePositiveAmount(amount);
        lock.lock();
        try {
            BigDecimal fee = calculateWithdrawalFee(amount);
            BigDecimal total = amount.add(fee);
            if (balance.compareTo(total) < 0) {
                Transaction failed = Transaction.builder()
                        .type(TransactionType.WITHDRAWAL)
                        .amount(amount)
                        .sourceAccountId(accountId)
                        .status(TransactionStatus.FAILED)
                        .description("Insufficient funds")
                        .build();
                transactionHistory.add(failed);
                throw new InsufficientFundsException(accountId, balance, total);
            }
            balance = balance.subtract(total);
            Transaction tx = Transaction.builder()
                    .type(TransactionType.WITHDRAWAL)
                    .amount(amount)
                    .sourceAccountId(accountId)
                    .status(TransactionStatus.SUCCESS)
                    .description(fee.compareTo(BigDecimal.ZERO) > 0
                            ? String.format("Withdrawal + fee %.2f", fee)
                            : "Withdrawal from account " + accountId)
                    .build();
            transactionHistory.add(tx);
            return tx;
        } finally {
            lock.unlock();
        }
    }

    protected BigDecimal calculateWithdrawalFee(BigDecimal amount) {
        return BigDecimal.ZERO;
    }

    public void recordTransaction(Transaction tx) {
        lock.lock();
        try {
            transactionHistory.add(tx);
        } finally {
            lock.unlock();
        }
    }

    public void adjustBalance(BigDecimal delta) {
        this.balance = this.balance.add(delta);
    }


    public ReentrantLock getLock() {
        if (lock == null) initTransientFields();
        return lock;
    }

    public BigDecimal getBalance()              { return balance; }
    public String getAccountId()                { return accountId; }
    public String getOwnerId()                  { return ownerId; }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public abstract String getAccountType();


    protected void validatePositiveAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, owner=%s, balance=%.2f]",
                getAccountType(), accountId, ownerId, balance);
    }
}