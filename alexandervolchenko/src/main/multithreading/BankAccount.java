package multithreading;

public class BankAccount {

  private final long id;
  private int balance;
  private final String accountName;

  public BankAccount(long id, int initialBalance, String accountName) {
    this.id = id;
    this.balance = initialBalance;
    this.accountName = accountName;
  }

  public long getId() {
    return id;
  }

  public int getBalance() {
    return balance;
  }

  public String getAccountName() {
    return accountName;
  }

  public void withdraw(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
    if (amount > balance) {
      throw new IllegalArgumentException("Insufficient funds");
    }
    balance -= amount;
  }

  public void deposit(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
    balance += amount;
  }

  @Override
  public String toString() {
    return String.format("Account[%s, id=%d, balance=%d]", accountName, id, balance);
  }
}
