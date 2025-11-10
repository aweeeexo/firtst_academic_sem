package io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

  /**
   * Разбивает файл на части указанного размера
   * @param sourcePath путь к исходному файлу
   * @param outputDir директория для сохранения частей
   * @param partSize размер каждой части в байтах
   * @return список путей к созданным частям
   */
  public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
    List<Path> partPaths = new ArrayList<>();
    Path sourceFile = Paths.get(sourcePath);
    String fileName = sourceFile.getFileName().toString();

    try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      long fileSize = sourceChannel.size();
      long bytesProcessed = 0;
      int partNumber = 1;

      ByteBuffer buffer = ByteBuffer.allocate(partSize);

      while (bytesProcessed < fileSize) {
        buffer.clear();
        int bytesRead = sourceChannel.read(buffer);

        if (bytesRead == -1) {
          break;
        }

        String partFileName = String.format("%s.part%d", fileName, partNumber);
        Path partPath = Paths.get(outputDir, partFileName);

        try (FileChannel partChannel = FileChannel.open(partPath,
            StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
          buffer.flip();
          partChannel.write(buffer);
        }

        partPaths.add(partPath);
        bytesProcessed += bytesRead;
        partNumber++;
      }
    }

    return partPaths;
  }

  /**
   * Объединяет части файла обратно в один файл
   * @param partPaths список путей к частям файла (в правильном порядке)
   * @param outputPath путь для результирующего файла
   */
  public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
    Path outputFile = Paths.get(outputPath);

    try (FileChannel outputChannel = FileChannel.open(outputFile,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

      for (Path partPath : partPaths) {
        if (!Files.exists(partPath)) {
          throw new IOException("Part file not found: " + partPath);
        }

        try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
          ByteBuffer buffer = ByteBuffer.allocate(8192);

          while (partChannel.read(buffer) > 0) {
            buffer.flip();
            outputChannel.write(buffer);
            buffer.clear();
          }
        }
      }
    }
  }
}