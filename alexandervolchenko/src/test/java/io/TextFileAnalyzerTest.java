package io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TextFileAnalyzerTest {

  @Test
  void testAnalyzeFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    // Создаем временный тестовый файл
    Path testFile = Files.createTempFile("test", ".txt");
    List<String> lines = Arrays.asList("Hello world!", "This is test.");
    Files.write(testFile, lines);

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

    // Проверки
    assertEquals(2, result.getLineCount());
    assertEquals(5, result.getWordCount());
    assertEquals(25, result.getCharCount());
    assertTrue(result.getCharFrequency().containsKey('H'));
    assertTrue(result.getCharFrequency().containsKey('e'));
    assertTrue(result.getCharFrequency().get('l') >= 3);
  }

  @Test
  void testSaveAnalysisResult() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    // Создаем тестовый результат
    java.util.HashMap<Character, Long> frequency = new java.util.HashMap<>();
    frequency.put('a', 5L);
    frequency.put('b', 3L);
    TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2L, 5L, 20L, frequency);

    // Сохранить в файл
    Path outputFile = Files.createTempFile("analysis", ".txt");
    analyzer.saveAnalysisResult(result, outputFile.toString());

    // Проверить что файл создан и содержит данные
    assertTrue(Files.size(outputFile) > 0);

    // Прочитать файл и проверить содержимое
    String content = Files.readString(outputFile);
    assertTrue(content.contains("Line count: 2"));
    assertTrue(content.contains("Word count: 5"));
    assertTrue(content.contains("Character count: 20"));
    assertTrue(content.contains("Character frequency: {a=5, b=3}"));
  }
}