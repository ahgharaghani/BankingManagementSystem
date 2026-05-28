package persistence;

import exception.PersistenceException;
import model.Account;
import util.BankLogger;

import java.io.*;
import java.util.logging.Logger;

/** Handles saving and loading the bank's snapshot */
public class PersistenceService {

    private static final Logger log = BankLogger.getLogger();
    private static final String DEFAULT_FILE = "bank_state.dat";

    private final String filePath;

    public PersistenceService() {
        this(DEFAULT_FILE);
    }

    public PersistenceService(String filePath) {
        this.filePath = filePath;
    }

    /** serializes the snapshot to disk */
    public void save(BankSnapshot snapshot) throws PersistenceException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(snapshot);
            log.info("model.Bank state saved to " + filePath);
        } catch (IOException e) {
            throw new PersistenceException("Failed to save bank state", e);
        }
    }

    /**  deserializes the snapshot from disk*/
    public BankSnapshot load() throws PersistenceException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.info("No saved state found at " + filePath + ". Starting fresh.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            BankSnapshot snapshot = (BankSnapshot) ois.readObject();
            for (Account account : snapshot.getAccounts()) {
                account.initTransientFields();
            }
            log.info("model.Bank state loaded from " + filePath);
            return snapshot;
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException("Failed to load bank state", e);
        }
    }

    public String getFilePath() { return filePath; }
}