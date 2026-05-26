package model;

import model.enums.TransactionStatus;
import model.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BigDecimal interestRate;

    public SavingsAccount(String accountId, String ownerId,
                          BigDecimal initialBalance, BigDecimal interestRate) {
        super(accountId, ownerId, initialBalance);
        this.interestRate = interestRate;
    }

    public Transaction applyInterest() {
        getLock().lock();
        try {
            BigDecimal interest = getBalance()
                    .multiply(interestRate)
                    .setScale(2, RoundingMode.HALF_UP);
            adjustBalance(interest);
            Transaction tx = Transaction.builder()
                    .type(TransactionType.INTEREST)
                    .amount(interest)
                    .targetAccountId(getAccountId())
                    .status(TransactionStatus.SUCCESS)
                    .description(String.format("Interest applied at rate %.4f", interestRate))
                    .build();
            recordTransaction(tx);
            return tx;
        } finally {
            getLock().unlock();
        }
    }

    public BigDecimal getInterestRate() { return interestRate; }

    @Override
    public String getAccountType() { return "SavingsAccount"; }
}