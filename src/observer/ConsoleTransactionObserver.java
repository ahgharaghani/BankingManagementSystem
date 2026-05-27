package observer;

import model.Transaction;

public class ConsoleTransactionObserver implements TransactionObserver {

    @Override
    public void onTransaction(Transaction transaction) {
        System.out.printf("[EVENT] %s%n", transaction);
    }
}