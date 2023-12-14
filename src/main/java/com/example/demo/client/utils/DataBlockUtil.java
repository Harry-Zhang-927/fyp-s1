package com.example.demo.client.utils;

import com.example.demo.parallel.CsvTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.ArrayList;

public class DataBlockUtil {
    public static String findAndRemoveBlockWithSignatureSequential(String csvPath, int rowsPerBlock, String signatureToFind) throws IOException {
        long startTime = System.currentTimeMillis();
        Path path = Paths.get(csvPath);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineCount = 0;
            List<String> currentBlock = new ArrayList<>();

            // 跳过表头
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                currentBlock.add(line);
                lineCount++;

                // 检查是否达到了数据块的结束，或者是否是文件的最后一行
                if (lineCount % (rowsPerBlock + 1) == 0 || !reader.ready()) {
                    // 搜索当前数据块中的MD5值
                    Optional<String> foundLine = currentBlock.stream()
                            .filter(l -> l.contains(signatureToFind))
                            .findFirst();

                    if (foundLine.isPresent()) {
                        // 从数据块中移除包含MD5的行
                        currentBlock.remove(foundLine.get());
                        String remainingBlock = String.join("\n", currentBlock);
                        long endTime = System.currentTimeMillis(); // 结束时间
                        System.out.println("Time taken: " + (endTime - startTime) + " ms"); // 打印所花时间
                        return MD5Util.calculateMD5(remainingBlock);

                        // 如果需要，可以在此处处理剩余的数据块，例如将其写回到文件中
                        // ...

                        // 如果只需要找到第一个匹配的数据块，则在此处终止循环
                    } else {
                        // 没有发现MD5，重置当前数据块以读取下一个数据块
                        currentBlock.clear();
                        lineCount = 0;
                    }
                }
            }
            System.out.println("MD5 " + signatureToFind + " not found in any block.");
            return  "hehe";
        }
    }

    public static void splitCsvToMetadataSequential(String inputCsvPath, int rowsPerBlock, String metadataPath, String outputCsvPath) throws IOException {
        Path inputPath = Paths.get(inputCsvPath);
        String inputFileName = inputPath.getFileName().toString();
        Path outputPath = Paths.get(outputCsvPath).resolve(inputFileName + "_tagged" + ".csv");

        // Will be replaced with Database
        Files.createDirectories(outputPath.getParent());

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             BufferedWriter metadataWriter = Files.newBufferedWriter(Paths.get(metadataPath), StandardCharsets.UTF_8);
             BufferedWriter outputWriter = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // read and save the header of the csv
            // Copy the header to the final 'tagged_with_metadata' csv
            if (line != null) {
                outputWriter.write(line);
                outputWriter.newLine();
            }
            while (line != null) {
                List<String> blockLines = new ArrayList<>();
                // read lines in group of block
                for (int rowCount = 0; rowCount < rowsPerBlock && line != null; rowCount++) {
                    line = reader.readLine(); //1000
                    if (line != null) {
                        blockLines.add(line);
                    }
                }
                // Calculate the whole file MD5
                String blockAsString = String.join("\n", blockLines);
                String blockMd5 = MD5Util.calculateMD5(blockAsString);
                byte[] signature = SecurityUtils.sign(SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE,
                        SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate(), blockMd5.getBytes());
                //tag
                Random rand = new Random();
                int n = rand.nextInt(blockLines.size()) + 1; // +1 to prevent the first line, to easy to break

                // insert the MD5 at line n
                blockLines.add(n, "Signature:" + Arrays.toString(signature));
                // will be replaced with Database
                metadataWriter.write("Signature: " + Arrays.toString(signature));
                metadataWriter.newLine();

                // append the data in the block into the final 'tagged_with_metadata' csv
                for (String blockLine : blockLines) {
                    outputWriter.write(blockLine);
                    outputWriter.newLine();
                }

            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Metadata generation and data block writing are done to " + outputPath);
    }

    public static void splitCsvToMetadataPar(String inputCsvPath, int rowsPerBlock, String metadataPath, String outputCsvPath) throws IOException {
        Path inputPath = Paths.get(inputCsvPath);
        String inputFileName = inputPath.getFileName().toString();
        Path outputPath = Paths.get(outputCsvPath).resolve(inputFileName + "_tagged" + ".csv");

        Files.createDirectories(outputPath.getParent());

        List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        RecursiveTask<List<String>> task = new CsvTask(lines, 0, lines.size(), rowsPerBlock);
        List<String> processedLines = forkJoinPool.invoke(task);

        try (BufferedWriter metadataWriter = Files.newBufferedWriter(Paths.get(metadataPath), StandardCharsets.UTF_8);
             BufferedWriter outputWriter = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            for (String line : processedLines) {
                outputWriter.write(line);
                outputWriter.newLine();
            }
        }
    }

    public static String findAndRemoveBlockWithSignatureParallel(String csvPath, int rowsPerBlock, String signatureToFind) throws IOException {
        long startTime = System.currentTimeMillis();
        Path path = Paths.get(csvPath);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        // 跳过表头
        List<String> dataLines = lines.subList(1, lines.size());
        // 将文件分成多个数据块，每个数据块包含 rowsPerBlock 行
        List<List<String>> blocks = new ArrayList<>();
        for (int i = 0; i < dataLines.size(); i += rowsPerBlock + 1) {
            int end = Math.min(i + rowsPerBlock + 1, dataLines.size());
            blocks.add(new ArrayList<>(dataLines.subList(i, end)));
        }
        // 使用并行流处理每个数据块
        Optional<String> result = blocks.parallelStream()
                .map(block -> {
                    Optional<String> foundLine = block.stream()
                            .filter(l -> l.contains(signatureToFind))
                            .findFirst();

                    if (foundLine.isPresent()) {
                        block.remove(foundLine.get());
                        String remainingBlock = String.join("\n", block);
                        return MD5Util.calculateMD5(remainingBlock);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst();

        if (result.isPresent()) {
            long endTime = System.currentTimeMillis(); // 结束时间
            System.out.println("Time taken: " + (endTime - startTime) + " ms"); // 打印所花时间
            return result.get();
        } else {
            System.out.println("MD5 " + signatureToFind + " not found in any block.");
            return "hehe";
        }
    }

    public static String findAndRemoveBlockWithSignatureParallel1(String csvPath, int rowsPerBlock, String signatureToFind) throws IOException {
        long startTime = System.currentTimeMillis();

        Path path = Paths.get(csvPath);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            List<String> block = new ArrayList<>();
            String result = null;

            // 读取文件头（如果有）
            String header = br.readLine();

            while ((line = br.readLine()) != null) {
                block.add(line);

                if (block.size() == rowsPerBlock) {
                    // 处理块
                    result = processBlock(block, signatureToFind);
                    if (result != null) {
                        break; // 找到签名，停止处理
                    }
                    block.clear(); // 准备下一个块
                }
            }

            // 处理最后一个块（如果有）
            if (result == null && !block.isEmpty()) {
                result = processBlock(block, signatureToFind);
            }

            long endTime = System.currentTimeMillis(); // 结束时间
            System.out.println("Time taken: " + (endTime - startTime) + " ms"); // 打印所花时间

            return (result != null) ? result : "Signature not found";
        }
    }

    private static String processBlock(List<String> block, String signatureToFind) {
        Optional<String> foundLine = block.stream()
                .filter(l -> l.contains(signatureToFind))
                .findFirst();

        if (foundLine.isPresent()) {
            block.remove(foundLine.get());
            String remainingBlock = String.join("\n", block);
            return MD5Util.calculateMD5(remainingBlock);
        }
        return null;
    }

    public static void compressFile(String filePath) throws IOException {
        String zipFile = filePath + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.putNextEntry(new ZipEntry(new File(filePath).getName()));

            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
        }

        Files.delete(Paths.get(filePath));
    }

    public static String findAndRemoveBlockWithSignaturePar(String csvPath, int rowsPerBlock, String signatureToFind) throws IOException {
        long startTime = System.currentTimeMillis();
        Path path = Paths.get(csvPath);
        AtomicBoolean found = new AtomicBoolean(false);
        String result = null;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<String> lines = reader.lines().skip(1).collect(Collectors.toList()); // 跳过表头
            List<ForkJoinTask<String>> tasks = new ArrayList<>();

            for (int i = 0; i < lines.size(); i += rowsPerBlock) {
                int end = Math.min(i + rowsPerBlock + 1, lines.size());
                BlockProcessor task = new BlockProcessor(new ArrayList<>(lines.subList(i, end)), signatureToFind, found);
                tasks.add(task.fork()); // 启动任务
            }

            for (ForkJoinTask<String> task : tasks) {
                if (!found.get()) {
                    String taskResult = task.join();
                    if (taskResult != null && found.compareAndSet(false, true)) {
                        result = taskResult;
                    }
                } else {
                    task.cancel(true); // 取消剩余未完成的任务
                }
            }
        }

        long endTime = System.currentTimeMillis(); // 结束时间
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        if (result != null) {
            return result;
        } else {
            System.out.println("MD5 " + signatureToFind + " not found in any block.");
            return "hehe";
        }
    }


    public static class BlockProcessor extends RecursiveTask<String> {
        private final List<String> block;
        private final String signatureToFind;

        public BlockProcessor(List<String> block, String signatureToFind, AtomicBoolean found) {
            this.block = block;
            this.signatureToFind = signatureToFind;
        }

        @Override
        protected String compute() {
            Optional<String> foundLine = block.stream()
                    .filter(l -> l.contains(signatureToFind))
                    .findFirst();

            if (foundLine.isPresent()) {
                block.remove(foundLine.get());
                String remainingBlock = String.join("\n", block);
                return MD5Util.calculateMD5(remainingBlock);
            }
            return null; // 如果没有找到签名
        }
    }

}

