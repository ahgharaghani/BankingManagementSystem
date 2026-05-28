package util;

import java.util.concurrent.atomic.AtomicLong;

/** Generates unique account IDs */
public final class AccountIdGenerator {

    private static final AtomicLong counter = new AtomicLong(100_000_000L);

    private AccountIdGenerator() {}

    /** Returns a new unique account number string. */
    public static String next() {
        return String.valueOf(counter.incrementAndGet());
    }

    /** Resets the counter to the given value (used when restoring persisted state). */
    public static void reset(long value) {
        counter.set(value);
    }

    public static long current() {
        return counter.get();
    }
}