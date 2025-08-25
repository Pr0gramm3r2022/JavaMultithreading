
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



class BankAccount {
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    // Deposit operation
    public void deposit(int amount) {
        balance += amount;
    }

    // Withdraw operation
    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        } else {
            System.out.println("Insufficient funds.");
            return false;
        }
    }

    // Get the lock for account
    public Lock getLock() {
        return lock;
    }

    // Check balance
    public int getBalance() {
        return balance;
    }
}

//creating an acccount object with a hardcorded isHeldByCurrentThread to determine the conditions that would cause them to be considered as such
/*class account {
    int balance;
    String name;
}

public account() {
    int
}*/

class Transaction implements Runnable {
    private BankAccount account1;
    private BankAccount account2;
    private int amount;
    private boolean simulateDeadlock;

    public Transaction(BankAccount account1, BankAccount account2, int amount, boolean simulateDeadlock) {
        this.account1 = account1;
        this.account2 = account2;
        this.amount = amount;
        this.simulateDeadlock = simulateDeadlock;
    }

    @Override
    public void run() {
        try {
            if (simulateDeadlock) {
                // Simulate Deadlock: Try to lock account1 then account2
                account1.getLock().lock();
                System.out.println(Thread.currentThread().getName() + " locked " + account1);
                Thread.sleep(100); // Simulate some work with account1

                account2.getLock().lock();
                System.out.println(Thread.currentThread().getName() + " locked " + account2);
            } else {
                // Resolve Deadlock: Try to acquire both locks with a timeout
                if (account1.getLock().tryLock() && account2.getLock().tryLock()) {
                    try {
                        // Perform transaction
                        if (account1.withdraw(amount)) {
                            account2.deposit(amount);
                            System.out.println(Thread.currentThread().getName() + " transferred " + amount + " from " + account1 + " to " + account2);
                        }
                    } finally {
                        account2.getLock().unlock();
                        account1.getLock().unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " couldn't lock both accounts. Transaction failed.");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } /*finally {
            if (account1.getLock().isHeldByCurrentThread()) {
                account1.getLock().unlock();
            }
            if (account2.getLock().isHeldByCurrentThread()) {
                account2.getLock().unlock();
            }*/ //had errors I was not able to resolve. Was trying to have a failsafe for the last exception.
        }
    }


public class Bank {
    public static void main(String[] args) {
        // Create two bank accounts
        BankAccount accountA = new BankAccount(1000);
        BankAccount accountB = new BankAccount(1000);

        //threads for transactions
        Thread t1 = new Thread(new Transaction(accountA, accountB, 100, true), "Transaction-1 (Deadlock)");
        Thread t2 = new Thread(new Transaction(accountB, accountA, 200, true), "Transaction-2 (Deadlock)");

        // Start both threads and observe deadlock
        t1.start();
        t2.start();

        // Wait for threads to finish
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create threads that avoid deadlock using timeout
        Thread t3 = new Thread(new Transaction(accountA, accountB, 50, false), "Transaction-3 (Resolved)");
        Thread t4 = new Thread(new Transaction(accountB, accountA, 150, false), "Transaction-4 (Resolved)");

        t3.start();
        t4.start();

        // Wait for threads to finish
        try {
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final balance in account A: " + accountA.getBalance());
        System.out.println("Final balance in account B: " + accountB.getBalance());
    }
}
