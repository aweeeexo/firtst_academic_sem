package patterns;

import java.util.Optional;

// TODO: Декоратор для валидации
//  Должен:
//      1. При findDataByKey - влидируются входные данные
//      2. При saveData - влидируются входные данные
//      3. При deleteData - влидируются входные данные
public class ValidationDecorator implements DataService {

  private final DataService wrappedService;

  public ValidationDecorator(DataService dataService) {
    this.wrappedService = dataService;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    validateKey(key);
    return wrappedService.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    validateKey(key);
    validateData(data);

    wrappedService.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    validateKey(key);

    return wrappedService.deleteData(key);
  }

  private void validateKey(String key) {
    if (key == null) {
      throw new IllegalArgumentException("Ключ не может быть null");
    }
    if (key.trim().isEmpty()) {
      throw new IllegalArgumentException("Ключ не может быть пустым");
    }
  }

  private void validateData(String data) {
    if (data == null) {
      throw new IllegalArgumentException("Данные не могут быть null");
    }
  }
  // todo: ваш код тут
}
