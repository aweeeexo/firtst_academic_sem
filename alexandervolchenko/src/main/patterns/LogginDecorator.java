package patterns;

import java.util.Optional;

// TODO: Декоратор для логирования
//  Должен:
//      1. При findDataByKey - логировать действие через System.out.println
//      2. При saveData - логировать действие через System.out.println
//      3. При deleteData - логировать действие через System.out.println
public class LoggingDecorator implements DataService {

  private final DataService wrappedService;


  public LoggingDecorator(DataService wrappedService) {
    this.wrappedService = wrappedService;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    System.out.println("Поиск данных по ключу: " + key);
    Optional<String> res = wrappedService.findDataByKey(key);

    if (res.isPresent()) {
      System.out.println("Данные найдены: " + res.get());
    } else {
      System.out.println("Данные не найдены для ключа: " + key);
    }

    return res;
  }

  @Override
  public void saveData(String key, String data) {
    System.out.println("Сохранение данных. Ключ: " + key + ", Данные: " + data);
    wrappedService.saveData(key, data);
    System.out.println("Данные успешно сохранены");
  }

  @Override
  public boolean deleteData(String key) {
    System.out.println("Удаление данных по ключу: " + key);
    boolean res = wrappedService.deleteData(key);
    if (res) {
      System.out.println("Данные успешно удалены");
    } else {
      System.out.println("Данные не найдены для удаления");
    }
    return res;
  }
  // todo: ваш код тут
}