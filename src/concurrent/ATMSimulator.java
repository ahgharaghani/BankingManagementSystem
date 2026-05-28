package concurrent;

import exception.*;
import model.Transaction;
import service.AccountService;
import util.BankLogger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/** Simulates an ATM terminal running as an independent thread */
public class ATMSimulator implements Runnable {

    private static final Logger log = BankLogger.getLogger();

    private final String atmId;
    private final AccountService accountService;
    private final List<String> accountIds;
    private final int operationCount;
    private final Random random = new Random();

    public ATMSimulator(String atmId, AccountService accountService,
                        List<String> accountIds, int operationCount) {
        this.atmId = atmId;
        this.accountService = accountService;
        this.accountIds = accountIds;
        this.operationCount = operationCount;
    }

    @Override
    public void run() {
        log.info(atmId + " started.");
        for (int i = 0; i < operationCount; i++) {
            try {
                int op = random.nextInt(3);
                String accountId = accountIds.get(random.nextInt(accountIds.size()));
                BigDecimal amount = BigDecimal.valueOf(10 + random.nextInt(200));

                switch (op) {
                    case 0 -> {
                        Transaction tx = accountService.deposit(accountId, amount);
                        System.out.printf("[%s] DEPOSIT  %-14s +%.2f  → bal=%.2f%n",
                                atmId, accountId, amount,
                                accountService.getAccount(accountId).getBalance());
                    }
                    case 1 -> {
                        Transaction tx = accountService.withdraw(accountId, amount);
                        System.out.printf("[%s] WITHDRAW %-14s -%.2f  → bal=%.2f%n",
                                atmId, accountId, amount,
                                accountService.getAccount(accountId).getBalance());
                    }
                    case 2 -> {
                        if (accountIds.size() < 2) break;
                        String targetId = accountIds.get(random.nextInt(accountIds.size()));
                        if (targetId.equals(accountId)) break;
                        Transaction tx = accountService.transfer(accountId, targetId, amount);
                        System.out.printf("[%s] TRANSFER %-14s → %-14s  %.2f%n",
                                atmId, accountId, targetId, amount);
                    }
                }
            } catch (InsufficientFundsException e) {
                System.out.printf("[%s] ⚠ %s%n", atmId, e.getMessage());
            } catch (TransferToSameAccountException | InvalidAmountException |
                     AccountNotFoundException e) {
                log.warning(atmId + ": " + e.getMessage());
            } catch (Exception e) {
                log.severe(atmId + " unexpected error: " + e.getMessage());
            }

            try { Thread.sleep(5); } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info(atmId + " finished.");
    }
}