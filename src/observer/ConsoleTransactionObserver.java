package observer;

import model.Transaction;

/** Observer that prints a summary of the transaction */
public class ConsoleTransactionObserver implements TransactionObserver {

    @Override
    public void onTransaction(Transaction transaction) {
        System.out.printf("[EVENT] %s%n", transaction);
    }
}