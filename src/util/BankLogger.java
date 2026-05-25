package util;

import java.io.IOException;
import java.util.logging.*;

public final class BankLogger {

    private static final String LOG_FILE = "transactions.log";
    private static final Logger LOGGER = Logger.getLogger("util.BankLogger");
    private static volatile boolean initialized = false;

    private BankLogger() {}

    public static Logger getLogger() {
        if (!initialized) {
            init();
        }
        return LOGGER;
    }

    private static synchronized void init() {
        if (initialized) return;
        LOGGER.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING);
        consoleHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE, true); // append
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.warning("Could not initialize file logger: " + e.getMessage());
        }

        LOGGER.setLevel(Level.ALL);
        initialized = true;
    }
}