package patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: Декоратор для кеширования
//  Должен:
//      1. При findDataByKey - кешировать результаты
//      2. При saveData - обновлять данные в кэше
//      3. При deleteData - инвалидировать кэш
public class CachingDecorator implements DataService {

  private final DataService wrappedService;

  private final Map<String, String> cache = new HashMap<>();

  public CachingDecorator(DataService wrappedService) {
    this.wrappedService = wrappedService;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    if (cache.containsKey(key)) {
      return Optional.of(cache.get(key));
    }
    Optional<String> res = wrappedService.findDataByKey(key);

    res.ifPresent(data -> cache.put(key, data));

    return res;
  }

  @Override
  public void saveData(String key, String data) {
    wrappedService.saveData(key, data);
    cache.put(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    cache.remove(key);
    return wrappedService.deleteData(key);
  }
  // todo: ваш код тут
}