package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final HashMap<Character, Long> charFrequency;

    public AnalysisResult(Long lineCount, Long wordCount, Long charCount,
        HashMap<Character, Long> charFrequency) {
      this.lineCount = lineCount;
      this.wordCount = wordCount;
      this.charCount = charCount;
      this.charFrequency = charFrequency;
    }

    public long getLineCount() {
      return lineCount;
    }

    public long getWordCount() {
      return wordCount;
    }

    public long getCharCount() {
      return charCount;
    }

    public HashMap<Character, Long> getCharFrequency() {
      return charFrequency;
    }

    @Override
    public String toString() {
      return String.format(
          "Analysis result:\n \tLine count: %d\n \tWord count: %d\n \tCharacter count: %d\n \tCharacter frequency: %s",
          lineCount, wordCount, charCount, charFrequency);
    }
  }

  public AnalysisResult analyzeFile(String filePath) throws IOException {
    long lineCount = 0;
    long wordCount = 0;
    long charCount = 0;
    HashMap<Character, Long> charFrequency = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lineCount++;
        charCount += line.length();

        if (!line.trim().isEmpty()) {
          String[] words = line.trim().split("\\s+");
          wordCount += words.length;
        }

        for (char c : line.toCharArray()) {
          charFrequency.put(c, charFrequency.getOrDefault(c, 0L) + 1);
        }
      }
    }

    return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
  }

  public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      writer.write(result.toString());
    }
  }
}