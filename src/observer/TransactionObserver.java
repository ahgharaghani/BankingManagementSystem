package observer;

import model.Transaction;

public interface TransactionObserver {

    void onTransaction(Transaction transaction);
}