import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ============================
// TODO: Define Custom Exception Classes
// ============================
class NegativeDepositException extends Exception {
    public NegativeDepositException(String message) {
        super(message);
    }
}

class OverdrawException extends Exception {
    public OverdrawException(String message) {
        super(message);
    }
}

class InvalidAccountOperationException extends Exception {
    public InvalidAccountOperationException(String message) {
        super(message);
    }
}

// ============================
// Observer Pattern - Define Observer Interface
// ============================

interface Observer {
    void update(String message);
}

// TODO: Implement TransactionLogger class (Concrete Observer)
class TransactionLogger implements Observer {
    public void update(String message) {
        System.out.println("[Transaction Log] " + message);
    }
}

// ============================
// BankAccount (Subject in Observer Pattern)
// ============================
class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected boolean isActive;
    private List<Observer> observers = new ArrayList<>();

    public BankAccount(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
        this.isActive = true;
    }

    // Attach observer
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Notify observers (TODO: Implement in methods)
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);// TODO: Notify all observers when a transaction occurs
        }
    }

    public void deposit(double amount) throws Exception {// TODO: Implement exception handling for negative deposits
        if (!isActive) throw new InvalidAccountOperationException("Account is closed.");
        if (amount < 0) throw new NegativeDepositException("Cannot deposit a negative amount.");
        balance += amount;
        notifyObservers("Deposited: $" + amount);
    }

    public void withdraw(double amount) throws Exception {// TODO: Implement exception handling for overdrawing and closed accounts
        if (!isActive) throw new InvalidAccountOperationException("Account is closed.");
        if (amount > balance) throw new OverdrawException("Insufficient balance.");
        balance -= amount;
        notifyObservers("Withdrew: $" + amount);
    }

    public double getBalance() {
        return balance;
    }

    public void closeAccount() {
        // TODO: Prevent further transactions when the account is closed
        isActive = false;
        notifyObservers("Account closed.");
    }
}

// ============================
// Decorator Pattern - Define SecureBankAccount Class
// ============================

abstract class BankAccountDecorator extends BankAccount {
    protected BankAccount decoratedAccount;

    public BankAccountDecorator(BankAccount account) {
        super(account.accountNumber, account.getBalance());
        this.decoratedAccount = account;
    }

    @Override
    public void addObserver(Observer observer) {
        decoratedAccount.addObserver(observer);
    }

    @Override
    public void notifyObservers(String message) {
        decoratedAccount.notifyObservers(message);
    }

    @Override
    public void closeAccount() {
        decoratedAccount.closeAccount();
    }

    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }
}

// TODO: Implement SecureBankAccount (Concrete Decorator)
class SecureBankAccount extends BankAccountDecorator {
    public SecureBankAccount(BankAccount account) {
        super(account);
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount > 500) {
            throw new OverdrawException("Cannot withdraw more than $500 at once.");
        }
        decoratedAccount.withdraw(amount);
    }

    @Override
    public void deposit(double amount) throws Exception {
        decoratedAccount.deposit(amount);
    }
}

// ============================
// Main Program
// ============================

public class BankAccountTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // TODO: Ask the user to enter an initial balance and create a BankAccount object
            // Example: System.out.print("Enter initial balance: ");
            //          double initialBalance = scanner.nextDouble();
            //          BankAccount account = new BankAccount("123456", initialBalance);
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            BankAccount rawAccount = new BankAccount("123456", initialBalance);
            System.out.println("Bank Account Created: #123456"); 

            // TODO: Create a TransactionLogger and attach it to the account
            TransactionLogger logger = new TransactionLogger();
            rawAccount.addObserver(logger);
            
            // TODO: Wrap account in SecureBankAccount decorator
            BankAccount secureAccount = new SecureBankAccount(rawAccount);

            // TODO: Allow the user to enter deposit and withdrawal amounts
            // Example: secureAccount.deposit(amount);
            System.out.print("Enter deposit amount: ");
            double depositAmount = scanner.nextDouble();
            secureAccount.deposit(depositAmount);
            // Example: secureAccount.withdraw(amount);
            System.out.print("Enter withdrawal amount: ");
            double withdrawAmount = scanner.nextDouble();
            secureAccount.withdraw(withdrawAmount);

            // TODO: Display the final balance
            System.out.println("Final Balance: $" + secureAccount.getBalance());

            

        } catch (Exception e) {
            // TODO: Catch and handle exceptions properly
            if (e instanceof NegativeDepositException) {
                System.out.println("[Error] Negative Deposit: " + e.getMessage());
            } else if (e instanceof OverdrawException) {
                System.out.println("[Error] Overdraw: " + e.getMessage());
            } else if (e instanceof InvalidAccountOperationException) {
                System.out.println("[Error] Invalid Account Operation: " + e.getMessage());
            } else {
                System.out.println("[Unexpected Error] " + e.getMessage());
            }
        } finally {
            scanner.close();
        }
    }
}