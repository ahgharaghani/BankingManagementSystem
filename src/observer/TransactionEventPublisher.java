package observer;

import model.Transaction;
import util.BankLogger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class TransactionEventPublisher {

    private static final Logger log = BankLogger.getLogger();

    private final List<TransactionObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    public void publish(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            try {
                observer.onTransaction(transaction);
            } catch (Exception e) {
                log.warning("Observer " + observer.getClass().getSimpleName()
                        + " threw an exception: " + e.getMessage());
            }
        }
    }
}