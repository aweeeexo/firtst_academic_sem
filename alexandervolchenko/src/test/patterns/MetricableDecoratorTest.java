package patterns;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetricableDecoratorTest {

  @Test
  void shouldSendMetricsForFindOperation() {
    DataService baseService = new SimpleDataService();
    MetricableDecorator metricService = new MetricableDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      metricService.saveData("test", "data");
      metricService.findDataByKey("test");

      String output = outputStream.toString();
      assertTrue(output.contains("Метод выполнялся: PT")); // Должен быть вывод метрик
    } finally {
      System.setOut(System.out);
    }
  }

  @Test
  void shouldSendMetricsForSaveOperation() {
    DataService baseService = new SimpleDataService();
    MetricableDecorator metricService = new MetricableDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      metricService.saveData("key1", "value1");

      String output = outputStream.toString();
      assertTrue(output.contains("Метод выполнялся: PT"));
    } finally {
      System.setOut(System.out);
    }
  }

  @Test
  void shouldSendMetricsForDeleteOperation() {
    DataService baseService = new SimpleDataService();
    MetricableDecorator metricService = new MetricableDecorator(baseService);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    try {
      metricService.saveData("key1", "value1");
      metricService.deleteData("key1");

      String output = outputStream.toString();
      assertTrue(output.contains("Метод выполнялся: PT"));
    } finally {
      System.setOut(System.out);
    }
  }

  @Test
  void shouldNotBreakBaseFunctionality() {
    DataService baseService = new SimpleDataService();
    MetricableDecorator metricService = new MetricableDecorator(baseService);

    metricService.saveData("test", "value");
    Optional<String> result = metricService.findDataByKey("test");
    boolean deleteResult = metricService.deleteData("test");

    assertTrue(result.isPresent());
    assertEquals("value", result.get());
    assertTrue(deleteResult);
  }
}