package patterns;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

class CachingDecoratorTest {

  @Test
  void shouldCacheFindResults() {
    DataService baseService = new SimpleDataService();
    CachingDecorator cachingService = new CachingDecorator(baseService);

    cachingService.saveData("key1", "value1");
    Optional<String> firstCall = cachingService.findDataByKey("key1");
    Optional<String> secondCall = cachingService.findDataByKey("key1");

    assertTrue(firstCall.isPresent());
    assertEquals("value1", firstCall.get());
    assertEquals("value1", secondCall.get());
  }

  @Test
  void shouldUpdateCacheOnSave() {
    DataService baseService = new SimpleDataService();
    CachingDecorator cachingService = new CachingDecorator(baseService);

    cachingService.saveData("key1", "value1");
    cachingService.saveData("key1", "value2");

    Optional<String> result = cachingService.findDataByKey("key1");
    assertTrue(result.isPresent());
    assertEquals("value2", result.get());
  }

  @Test
  void shouldInvalidateCacheOnDelete() {
    DataService baseService = new SimpleDataService();
    CachingDecorator cachingService = new CachingDecorator(baseService);

    cachingService.saveData("key1", "value1");
    cachingService.findDataByKey("key1");
    cachingService.deleteData("key1");

    Optional<String> result = cachingService.findDataByKey("key1");
    assertFalse(result.isPresent());
  }

  @Test
  void shouldReturnEmptyForNonExistentKey() {
    DataService baseService = new SimpleDataService();
    CachingDecorator cachingService = new CachingDecorator(baseService);

    Optional<String> result = cachingService.findDataByKey("non-existent");

    assertFalse(result.isPresent());
  }
}