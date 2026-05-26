package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckingAccount extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BigDecimal withdrawalFeeRate;

    public CheckingAccount(String accountId, String ownerId,
                           BigDecimal initialBalance, BigDecimal withdrawalFeeRate) {
        super(accountId, ownerId, initialBalance);
        this.withdrawalFeeRate = withdrawalFeeRate;
    }

    @Override
    public BigDecimal calculateWithdrawalFee(BigDecimal amount) {
        return amount.multiply(withdrawalFeeRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getWithdrawalFeeRate() { return withdrawalFeeRate; }

    @Override
    public String getAccountType() { return "CheckingAccount"; }
}