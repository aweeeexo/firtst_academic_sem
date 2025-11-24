package patterns;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ValidationDecoratorTest {

  @Test
  void shouldThrowExceptionForNullKeyOnFind() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.findDataByKey(null);
    });
  }

  @Test
  void shouldThrowExceptionForEmptyKeyOnFind() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.findDataByKey("");
    });

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.findDataByKey("   ");
    });
  }

  @Test
  void shouldThrowExceptionForNullDataOnSave() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.saveData("validKey", null);
    });
  }

  @Test
  void shouldThrowExceptionForInvalidKeyOnSave() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.saveData(null, "validData");
    });

    assertThrows(IllegalArgumentException.class, () -> {
      validationService.saveData("", "validData");
    });
  }

  @Test
  void shouldAllowValidOperations() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    validationService.saveData("validKey", "validData");
    Optional<String> result = validationService.findDataByKey("validKey");
    boolean deleteResult = validationService.deleteData("validKey");

    assertTrue(result.isPresent());
    assertEquals("validData", result.get());
    assertTrue(deleteResult);
  }

  @Test
  void shouldAllowEmptyStringAsData() {
    DataService baseService = new SimpleDataService();
    ValidationDecorator validationService = new ValidationDecorator(baseService);

    assertDoesNotThrow(() -> {
      validationService.saveData("key", "");
    });
  }
}