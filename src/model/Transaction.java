package model;

import model.enums.TransactionStatus;
import model.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String transactionId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String sourceAccountId;
    private final String targetAccountId;
    private TransactionStatus status;
    private final String description;

    private Transaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.type = builder.type;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
        this.sourceAccountId = builder.sourceAccountId;
        this.targetAccountId = builder.targetAccountId;
        this.status = builder.status;
        this.description = builder.description;
    }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public TransactionStatus getStatus(){ return status; }
    public String getDescription() { return description; }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | amount=%.2f | from=%s | to=%s | status=%s | %s",
                timestamp, transactionId, type, amount,
                sourceAccountId != null ? sourceAccountId : "-",
                targetAccountId != null ? targetAccountId : "-",
                status, description != null ? description : "");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String transactionId = UUID.randomUUID().toString();
        private TransactionType type;
        private BigDecimal amount;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String sourceAccountId;
        private String targetAccountId;
        private TransactionStatus status = TransactionStatus.PENDING;
        private String description;

        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder timestamp(LocalDateTime ts) { this.timestamp = ts; return this; }
        public Builder sourceAccountId(String id) { this.sourceAccountId = id; return this; }
        public Builder targetAccountId(String id) { this.targetAccountId = id; return this; }
        public Builder status(TransactionStatus status) { this.status = status; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public Transaction build() {
            if (type == null || amount == null) {
                throw new IllegalStateException("Transaction type and amount are required.");
            }
            return new Transaction(this);
        }
    }
}