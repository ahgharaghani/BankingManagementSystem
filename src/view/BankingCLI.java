package view;

import exception.*;
import model.*;
import service.AccountService;
import service.CustomerService;

import java.math.BigDecimal;
import java.util.Scanner;

/** Command-line interface for the banking system */
public class BankingCLI {

    private final Bank bank;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final Scanner scanner;

    public BankingCLI() {
        this.bank = Bank.getInstance();
        this.customerService = bank.getCustomerService();
        this.accountService  = bank.getAccountService();
        this.scanner = new Scanner(System.in);
    }

    // === Entry point =========================================================

    public void start() {
        tryLoadState();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1"  -> createCustomer();
                case "2"  -> openAccount();
                case "3"  -> deposit();
                case "4"  -> withdraw();
                case "5"  -> transfer();
                case "6"  -> showCustomer();
                case "7"  -> showAccount();
                case "8"  -> { trySaveState(); running = false; }
                default   -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Goodbye.");
    }

    // === Menu =========================================================

    private void printMenu() {
        System.out.println();
        System.out.println("=== Banking System ===");
        System.out.println("1. Register customer");
        System.out.println("2. Open account");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. Show customer info");
        System.out.println("7. Show account info");
        System.out.println("8. Save & Exit");
        System.out.print("Choose: ");
    }

    // === Operations =========================================================

    private void createCustomer() {
        System.out.print("National ID: ");
        String nationalId = scanner.nextLine().trim();
        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();

        try {
            Customer customer = customerService.registerCustomer(nationalId, firstName, lastName);
            System.out.println("Customer registered: " + customer);
        } catch (DuplicateCustomerException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void openAccount() {
        System.out.print("National ID: ");
        String nationalId = scanner.nextLine().trim();
        System.out.print("Account type (1=Savings, 2=Checking): ");
        String type = scanner.nextLine().trim();
        System.out.print("Initial balance: ");
        BigDecimal initial = parseBigDecimal(scanner.nextLine().trim());
        if (initial == null) return;

        try {
            if (type.equals("1")) {
                System.out.print("Interest rate (e.g. 0.05): ");
                BigDecimal rate = parseBigDecimal(scanner.nextLine().trim());
                if (rate == null) return;
                Account account = accountService.openSavingsAccount(nationalId, initial, rate);
                System.out.println("Savings account opened: " + account);
            } else if (type.equals("2")) {
                System.out.print("Fee rate (e.g. 0.01): ");
                BigDecimal rate = parseBigDecimal(scanner.nextLine().trim());
                if (rate == null) return;
                Account account = accountService.openCheckingAccount(nationalId, initial, rate);
                System.out.println("Checking account opened: " + account);
            } else {
                System.out.println("Invalid account type.");
            }
        } catch (CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deposit() {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = parseBigDecimal(scanner.nextLine().trim());
        if (amount == null) return;

        try {
            Transaction tx = accountService.deposit(accountId, amount);
            System.out.println("Deposit successful: " + tx);
        } catch (AccountNotFoundException | InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = parseBigDecimal(scanner.nextLine().trim());
        if (amount == null) return;

        try {
            Transaction tx = accountService.withdraw(accountId, amount);
            System.out.println("Withdrawal successful: " + tx);
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.print("Source account ID: ");
        String sourceId = scanner.nextLine().trim();
        System.out.print("Target account ID: ");
        String targetId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = parseBigDecimal(scanner.nextLine().trim());
        if (amount == null) return;

        try {
            Transaction tx = accountService.transfer(sourceId, targetId, amount);
            System.out.println("Transfer successful: " + tx);
        } catch (AccountNotFoundException | TransferToSameAccountException
                 | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showCustomer() {
        System.out.print("National ID: ");
        String nationalId = scanner.nextLine().trim();

        try {
            Customer customer = customerService.getCustomer(nationalId);
            System.out.println(customer);
            System.out.println("Accounts: " + customer.getAccountIds());
        } catch (CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showAccount() {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        try {
            Account account = accountService.getAccount(accountId);
            System.out.println(account);
            System.out.println("Transaction history:");
            account.getTransactionHistory().forEach(tx -> System.out.println("  " + tx));
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // === Persistence =========================================================

    private void tryLoadState() {
        try {
            bank.load();
            System.out.println("Previous state loaded.");
        } catch (Exception e) {
            System.out.println("No saved state found. Starting fresh.");
        }
    }

    private void trySaveState() {
        try {
            bank.save();
            System.out.println("State saved.");
        } catch (Exception e) {
            System.out.println("Warning: could not save state. " + e.getMessage());
        }
    }

    // === Helpers =========================================================

    /** parses a BigDecimal from input; returns null and prints error on failure */
    private BigDecimal parseBigDecimal(String input) {
        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + input);
            return null;
        }
    }
}
