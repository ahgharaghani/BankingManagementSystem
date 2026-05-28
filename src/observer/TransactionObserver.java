package observer;

import model.Transaction;

/** Observer interface */
public interface TransactionObserver {
    /** called after each transaction */
    void onTransaction(Transaction transaction);
}