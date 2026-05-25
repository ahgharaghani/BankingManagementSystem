package util;

import java.util.concurrent.atomic.AtomicLong;

public final class AccountIdGenerator {

    private static final AtomicLong counter = new AtomicLong(100_000_000L);

    private AccountIdGenerator() {}

    public static String next() {
        return String.valueOf(counter.incrementAndGet());
    }

    public static void reset(long value) {
        counter.set(value);
    }

    public static long current() {
        return counter.get();
    }
}