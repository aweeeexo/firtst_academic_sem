package patterns;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class LoggingDecoratorTest {

  @Test
  void shouldLogFindOperation() {
    DataService baseService = new SimpleDataService();
    LoggingDecorator loggingService = new LoggingDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    try {
      loggingService.saveData("test", "data");
      loggingService.findDataByKey("test");

      String output = outputStream.toString();
      assertTrue(output.contains("Поиск данных по ключу: test"));
      assertTrue(output.contains("Данные найдены: data"));
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void shouldLogSaveOperation() {
    DataService baseService = new SimpleDataService();
    LoggingDecorator loggingService = new LoggingDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      loggingService.saveData("key1", "value1");

      String output = outputStream.toString();
      assertTrue(output.contains("Сохранение данных. Ключ: key1, Данные: value1"));
      assertTrue(output.contains("Данные успешно сохранены"));
    } finally {
      System.setOut(System.out);
    }
  }

  @Test
  void shouldLogDeleteOperation() {
    DataService baseService = new SimpleDataService();
    LoggingDecorator loggingService = new LoggingDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      loggingService.saveData("key1", "value1");
      loggingService.deleteData("key1");

      String output = outputStream.toString();
      assertTrue(output.contains("Удаление данных по ключу: key1"));
      assertTrue(output.contains("Данные успешно удалены"));
    } finally {
      System.setOut(System.out);
    }
  }

  @Test
  void shouldLogNotFoundForMissingKey() {
    DataService baseService = new SimpleDataService();
    LoggingDecorator loggingService = new LoggingDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      loggingService.findDataByKey("missing");

      String output = outputStream.toString();
      assertTrue(output.contains("Данные не найдены для ключа: missing"));
    } finally {
      System.setOut(System.out);
    }
  }
}