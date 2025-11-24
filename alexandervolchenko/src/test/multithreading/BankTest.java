package multithreading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

  @Test
  void testBasicTransfer() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");
    BankAccount account2 = new BankAccount(2, 500, "Account2");

    bank.sendToAccount(account1, account2, 200);

    assertEquals(800, account1.getBalance());
    assertEquals(700, account2.getBalance());
  }

  @Test
  void testInsufficientFunds() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 100, "Account1");
    BankAccount account2 = new BankAccount(2, 500, "Account2");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(account1, account2, 200));

    assertTrue(exception.getMessage().contains("Insufficient funds"));
    assertEquals(100, account1.getBalance());
    assertEquals(500, account2.getBalance());
  }

  @Test
  void testInvalidAmount() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");
    BankAccount account2 = new BankAccount(2, 500, "Account2");

    assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(account1, account2, -100));

    assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(account1, account2, 0));
  }

  @Test
  void testNullAccounts() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");

    assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(null, account1, 100));

    assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(account1, null, 100));
  }

  @Test
  void testSameAccountTransfer() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");

    assertThrows(IllegalArgumentException.class,
        () -> bank.sendToAccount(account1, account1, 100));
  }

  @Test
  @Timeout(5) // Тест должен завершиться за 5 секунд
  void testMultiThreadedTransfers() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 10000, "Account1");
    BankAccount account2 = new BankAccount(2, 10000, "Account2");

    int threadCount = 10;
    int transfersPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    for (int i = 0; i < threadCount; i++) {
      final int threadId = i;
      executor.submit(() -> {
        try {
          for (int j = 0; j < transfersPerThread; j++) {
            try {
              if (threadId % 2 == 0) {
                bank.sendToAccount(account1, account2, 10);
              } else {
                bank.sendToAccount(account2, account1, 5);
              }
              successCount.incrementAndGet();
            } catch (IllegalArgumentException e) {
              errorCount.incrementAndGet();
              // Ожидаемые ошибки при недостатке средств
            }
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    System.out.println("Successful transfers: " + successCount.get());
    System.out.println("Failed transfers: " + errorCount.get());
    System.out.println("Final balance - " + account1.getAccountName() +
        ": " + account1.getBalance() + ", " +
        account2.getAccountName() + ": " + account2.getBalance());

    assertEquals(20000, account1.getBalance() + account2.getBalance());
  }

  @Test
  @Timeout(3) // Тест должен завершиться за 3 секунды (если нет deadlock)
  void testDeadlockScenario() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");
    BankAccount account2 = new BankAccount(2, 1000, "Account2");

    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger completedThreads = new AtomicInteger(0);

    // Поток 1: перевод от account1 к account2
    Thread thread1 = new Thread(() -> {
      try {
        bank.sendToAccountDeadLock(account1, account2, 100);
        completedThreads.incrementAndGet();
      } catch (Exception e) {
        System.out.println("Thread 1 error: " + e.getMessage());
      } finally {
        latch.countDown();
      }
    }, "Thread-1");

    // Поток 2: перевод от account2 к account1 (обратное направление)
    Thread thread2 = new Thread(() -> {
      try {
        bank.sendToAccountDeadLock(account2, account1, 50);
        completedThreads.incrementAndGet();
      } catch (Exception e) {
        System.out.println("Thread 2 error: " + e.getMessage());
      } finally {
        latch.countDown();
      }
    }, "Thread-2");

    thread1.start();
    thread2.start();

    boolean completed = latch.await(2, TimeUnit.SECONDS);

    if (!completed) {
      System.out.println("DEADLOCK DETECTED! Threads did not complete in time.");
    } else {
      System.out.println("Both threads completed successfully");
    }

    System.out.println("Completed threads: " + completedThreads.get());
  }

  @Test
  void testOrderedLockingPreventsDeadlock() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000, "Account1");
    BankAccount account2 = new BankAccount(2, 1000, "Account2");
    BankAccount account3 = new BankAccount(3, 1000, "Account3");

    int threadCount = 6;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    Runnable transfer1 = () -> {
      try {
        bank.sendToAccount(account1, account2, 10);
      } catch (Exception e) {
      } finally {
        latch.countDown();
      }
    };

    Runnable transfer2 = () -> {
      try {
        bank.sendToAccount(account2, account1, 5);
      } catch (Exception e) {
      } finally {
        latch.countDown();
      }
    };

    Runnable transfer3 = () -> {
      try {
        bank.sendToAccount(account1, account3, 15);
      } catch (Exception e) {
      } finally {
        latch.countDown();
      }
    };

    executor.execute(transfer1);
    executor.execute(transfer2);
    executor.execute(transfer3);
    executor.execute(transfer1);
    executor.execute(transfer2);
    executor.execute(transfer3);

    boolean allCompleted = latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    assertTrue(allCompleted, "All threads should complete without deadlock");
    assertEquals(3000, account1.getBalance() + account2.getBalance() + account3.getBalance());
  }
}