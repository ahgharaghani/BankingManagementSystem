# 🏦 Concurrent Banking Management System

A thread-safe, object-oriented banking management system implemented in Java, featuring concurrent transaction processing, custom exception handling, event-driven logging, and state persistence.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Design Patterns](#design-patterns)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Concurrency Model](#concurrency-model)
- [Exception Handling](#exception-handling)
- [Persistence](#persistence)
- [Logging](#logging)

---

## Overview

This project is a simplified simulator of a banking management system designed to handle core banking operations in a multi-threaded, thread-safe environment. The system manages customers, bank accounts (savings and checking), and financial transactions while ensuring data integrity under concurrent access.

The project demonstrates practical application of software engineering principles including object-oriented design, concurrency control, design patterns, custom exception management, event-driven architecture, and data persistence.

---

## Features

- **Customer Management** — Register customers with unique national IDs; each customer can own multiple accounts
- **Account Management** — Open savings accounts (with interest) and checking accounts (with withdrawal fees)
- **Financial Operations** — Deposit, withdraw, and transfer money between accounts
- **Thread-Safe Transactions** — All financial operations are protected against race conditions using `ReentrantLock`
- **Deadlock Prevention** — Transfer operations use ordered locking to prevent deadlocks
- **ATM Simulator** — Multiple concurrent ATM threads perform random operations simultaneously
- **Observer Pattern** — Event-driven transaction notifications via the Observer pattern
- **State Persistence** — Save and restore the entire system state using Java Serialization
- **Comprehensive Logging** — All transactions and events logged to `transactions.log`
- **Custom Exceptions** — Meaningful, domain-specific exception hierarchy for all error scenarios

---

## Design Patterns

| Pattern           | Where Used                                                                         | Description                                                                                                                   |
| ----------------- | ---------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **Singleton**     | `Bank`                                                                             | Double-checked locking ensures a single bank instance across the entire system, even in multi-threaded contexts               |
| **Observer**      | `TransactionObserver` / `TransactionEventPublisher` / `ConsoleTransactionObserver` | Decouples transaction events from their consumers; new observers (e.g., email, SMS) can be added without modifying core logic |
| **Builder**       | `Transaction`                                                                      | Provides a clean, readable API for constructing complex transaction objects with optional fields                              |
| **Repository**    | `AccountRepository` / `CustomerRepository`                                         | Abstracts data storage behind a clean interface using `ConcurrentHashMap` for thread-safe access                              |
| **Service Layer** | `AccountService` / `CustomerService`                                               | Encapsulates business logic and orchestrates operations between repositories and other components                             |

---

## Architecture

```
┌──────────────────────────────────────────────────────┐
│                    view / CLI                         │
│                  (BankingCLI)                         │
├──────────────────────────────────────────────────────┤
│                  service layer                        │
│          ┌──────────────┬──────────────┐              │
│          │ AccountService│CustomerService│             │
│          └──────┬───────┴──────┬───────┘              │
├─────────────────┼──────────────┼─────────────────────┤
│            repository layer     │                      │
│     ┌──────────────┐  ┌────────▼────────┐            │
│     │AccountRepo   │  │CustomerRepo     │            │
│     └──────┬───────┘  └────────┬────────┘            │
├─────────────┼───────────────────┼────────────────────┤
│             │     model layer   │                      │
│    ┌────────▼───────────────────▼────────┐            │
│    │  Bank (Singleton)                   │            │
│    │  ├── Account (abstract)             │            │
│    │  │   ├── SavingsAccount             │            │
│    │  │   └── CheckingAccount            │            │
│    │  ├── Customer                       │            │
│    │  └── Transaction (Builder)          │            │
│    └─────────────────────────────────────┘            │
├──────────────────────────────────────────────────────┤
│              cross-cutting concerns                    │
│  ┌─────────────┐ ┌────────────┐ ┌─────────────────┐ │
│  │  exception   │ │  observer   │ │  persistence    │ │
│  └─────────────┘ └────────────┘ └─────────────────┘ │
│  ┌─────────────┐ ┌──────────────────────────────┐   │
│  │   util       │ │       concurrent             │   │
│  └─────────────┘ └──────────────────────────────┘   │
└──────────────────────────────────────────────────────┘
```

---

## Project Structure

```
src/
├── Main.java                          # Application entry point
├── model/
│   ├── Bank.java                      # Singleton — central system coordinator
│   ├── Account.java                   # Abstract base class for accounts
│   ├── SavingsAccount.java            # Savings account with interest rate
│   ├── CheckingAccount.java           # Checking account with withdrawal fee
│   ├── Customer.java                  # Customer entity
│   ├── Transaction.java               # Immutable transaction record (Builder pattern)
│   └── enums/
│       ├── TransactionType.java       # DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN, FEE, INTEREST
│       └── TransactionStatus.java     # SUCCESS, FAILED, PENDING
├── service/
│   ├── AccountService.java            # Account operations & financial logic
│   └── CustomerService.java           # Customer registration & lookup
├── repository/
│   ├── AccountRepository.java         # Thread-safe account data store
│   └── CustomerRepository.java        # Thread-safe customer data store
├── exception/
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   ├── AccountNotFoundException.java
│   ├── CustomerNotFoundException.java
│   ├── DuplicateCustomerException.java
│   ├── TransferToSameAccountException.java
│   └── PersistenceException.java
├── observer/
│   ├── TransactionObserver.java       # Observer interface
│   ├── TransactionEventPublisher.java # Publisher with CopyOnWriteArrayList
│   └── ConsoleTransactionObserver.java# Console output observer
├── persistence/
│   ├── PersistenceService.java        # Serialization save/load
│   └── BankSnapshot.java              # Snapshot DTO for persistence
├── concurrent/
│   └── ATMSimulator.java              # ATM thread simulator
├── util/
│   ├── AccountIdGenerator.java        # AtomicLong-based unique ID generator
│   └── BankLogger.java                # Dual-handler logger (console + file)
└── view/
    └── BankingCLI.java                # Interactive command-line interface
```

---

## Getting Started

### Prerequisites

- **Java 17** or later (uses `switch` expressions and pattern matching)
- No external dependencies — uses only the Java standard library

### Compilation & Run

```bash
# Compile
javac -d out src/**/*.java src/*.java

# Run
java -cp out Main
```

Or simply open the project in **IntelliJ IDEA** and run `Main.java`.

---

## Usage

When you run the program, an interactive CLI menu appears:

```
=== Banking System ===
1. Register customer
2. Open account
3. Deposit
4. Withdraw
5. Transfer
6. Show customer info
7. Show account info
8. Save & Exit
Choose:
```

### Example Workflow

1. **Register a customer** — Enter national ID, first name, and last name
2. **Open an account** — Choose Savings or Checking, provide initial balance and rate/fee
3. **Deposit / Withdraw / Transfer** — Perform financial operations using account IDs
4. **View information** — Look up customer details or account history
5. **Save & Exit** — Persist the system state for the next session

---

## Concurrency Model

### Thread Safety

| Component            | Mechanism                              | Purpose                                           |
| -------------------- | -------------------------------------- | ------------------------------------------------- |
| `Account` operations | `ReentrantLock` (fair) per account     | Prevents race conditions on balance modifications |
| `Bank` Singleton     | Double-checked locking with `volatile` | Ensures single instance across threads            |
| `AccountIdGenerator` | `AtomicLong`                           | Lock-free unique ID generation                    |
| Repositories         | `ConcurrentHashMap`                    | Thread-safe data storage                          |
| Observer list        | `CopyOnWriteArrayList`                 | Safe iteration during concurrent notifications    |

### Deadlock Prevention in Transfers

The `transfer()` method in `AccountService` locks the two involved accounts in a **deterministic order** (sorted by account ID). This eliminates circular wait conditions:

```java
Account first  = sourceId.compareTo(targetId) < 0 ? source : target;
Account second = sourceId.compareTo(targetId) < 0 ? target : source;

lock1.lock();
try {
    lock2.lock();
    try {
        // atomic transfer logic
    } finally { lock2.unlock(); }
} finally { lock1.unlock(); }
```

### ATM Simulator

The `ATMSimulator` class models independent ATM terminals, each running as a separate thread. Multiple simulators can perform deposits, withdrawals, and transfers concurrently on shared accounts — demonstrating the system's thread safety under real-world-like conditions.

---

## Exception Handling

The system uses a hierarchy of custom exceptions that provide meaningful error messages:

| Exception                        | Trigger                                                           |
| -------------------------------- | ----------------------------------------------------------------- |
| `InsufficientFundsException`     | Withdrawal or transfer exceeds available balance (including fees) |
| `InvalidAmountException`         | Amount is null, zero, or negative                                 |
| `AccountNotFoundException`       | Referenced account ID does not exist                              |
| `CustomerNotFoundException`      | Referenced national ID does not exist                             |
| `DuplicateCustomerException`     | A customer with the same national ID already exists               |
| `TransferToSameAccountException` | Source and destination accounts are the same                      |
| `PersistenceException`           | File I/O failure during save or load operations                   |

All exceptions are caught and handled gracefully — the program never crashes unexpectedly.

---

## Persistence

The system uses **Java Serialization** to save and restore its complete state:

- **Save**: Serializes all customers, accounts, and the ID counter into `bank_state.dat`
- **Load**: Deserializes the snapshot on startup and reinitializes transient fields (e.g., `ReentrantLock` instances)
- **Safety**: Runtime components (`Thread`, `Lock`, `Logger`) are never serialized — they are reconstructed after deserialization via `initTransientFields()`

---

## Logging

All transactions and significant events are logged using `java.util.logging`:

- **Console handler**: Displays warnings and severe messages (`WARNING` level and above)
- **File handler**: Records everything to `transactions.log` (`ALL` level)
- Logs include transaction ID, type, amount, source/target accounts, status, and timestamps
- Both successful and failed transactions are recorded for audit purposes

---

## Key Design Decisions

1. **`BigDecimal` for monetary values** — Avoids floating-point precision errors inherent to `double` and `float`
2. **Fair `ReentrantLock`** — Ensures FIFO ordering of waiting threads, preventing thread starvation
3. **Immutable `Transaction`** — Built via the Builder pattern; once created, transaction records cannot be modified (except status updates)
4. **Observer isolation** — Exceptions in observers are caught and logged without disrupting the core banking flow
5. **Separation of concerns** — Model, service, repository, view, and persistence layers are cleanly separated

---

## Team

**Hamrah Academy's first Group — Computer Engineering Students**

AmirHossein Qaraqani

Matin Nameni

Mohammad Rajaei

---

## License

*This project was developed as a **Hamrah Academy** (MCI) project.*
