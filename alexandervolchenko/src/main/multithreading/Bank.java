package multithreading;

public class Bank {

  // Метод, подверженный deadlock - неправильный порядок синхронизации
  public void sendToAccountDeadLock(BankAccount from, BankAccount to, int amount) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Accounts cannot be null");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    System.out.println(Thread.currentThread().getName() +
        " attempting transfer: " + amount + " from " +
        from.getAccountName() + " to " + to.getAccountName());

    synchronized (from) {
      System.out.println(Thread.currentThread().getName() + " locked " + from.getAccountName());

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }

      synchronized (to) {
        System.out.println(Thread.currentThread().getName() + " locked " + to.getAccountName());

        if (from.getBalance() < amount) {
          throw new IllegalArgumentException("Insufficient funds in " + from.getAccountName());
        }

        from.withdraw(amount);
        to.deposit(amount);

        System.out.println(Thread.currentThread().getName() +
            " completed transfer: " + amount + " from " +
            from.getAccountName() + " to " + to.getAccountName());
      }
    }
  }

  //Корректный метод перевода без deadlock
  public void sendToAccount(BankAccount from, BankAccount to, int amount) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Accounts cannot be void");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (from == to) {
      throw new IllegalArgumentException("Cannot transfer to the same account");
    }

    BankAccount firstLock = from.getId() < to.getId() ? from : to;
    BankAccount secondLock = from.getId() < to.getId() ? to : from;

    synchronized (firstLock) {
      synchronized (secondLock) {
        if (from.getBalance() < amount) {
          throw new IllegalArgumentException(
              "Insufficient funds in " + from.getAccountName() +
                  ". Required: " + amount + ", Available: " + from.getBalance());
        }

        from.withdraw(amount);
        to.deposit(amount);

        System.out.println(Thread.currentThread().getName() +
            " successfully transferred: " + amount + " from " +
            from.getAccountName() + " to " + to.getAccountName() +
            " | Balance: " + from.getAccountName() + "=" +
            from.getBalance() + ", " + to.getAccountName() + "=" + to.getBalance());
      }
    }
  }
}
