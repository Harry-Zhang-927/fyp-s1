package com.example.demo.client.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DataBlockUtil {

//    public static List<DataBlock> divideData(byte[] data, int blockSize) {
//        List<DataBlock> blocks = new ArrayList<>();
//        int start = 0;
//        while (start < data.length) {
//            int end = Math.min(data.length, start + blockSize);
//            blocks.add(new DataBlock(Arrays.copyOfRange(data, start, end)));
//            start += blockSize;
//        }
//        return blocks;
//    }

    public static String findAndRemoveBlockWithSignature(String csvPath, int rowsPerBlock, String signatureToFind) throws IOException {
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

    public static void splitCsvToMetadata(String inputCsvPath, int rowsPerBlock, String metadataPath, String outputCsvPath) throws IOException {
        Path inputPath = Paths.get(inputCsvPath);
        String inputFileName = inputPath.getFileName().toString();
        Path outputPath = Paths.get(outputCsvPath).resolve(inputFileName + "_tagged" + ".csv");

        // 确保输出文件的目录存在，如果不存在则创建
        Files.createDirectories(outputPath.getParent());

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             BufferedWriter metadataWriter = Files.newBufferedWriter(Paths.get(metadataPath), StandardCharsets.UTF_8);
             BufferedWriter outputWriter = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // 读取表头

            // 写入表头到最终的CSV文件
            if (line != null) {
                outputWriter.write(line);
                outputWriter.newLine();
            }

            while (line != null) {
                List<String> blockLines = new ArrayList<>();
                // 读取数据块
                for (int rowCount = 0; rowCount < rowsPerBlock && line != null; rowCount++) {
                    line = reader.readLine();
                    if (line != null) {
                        blockLines.add(line);
                    }
                }

                // 计算整个数据块的MD5
                String blockAsString = String.join("\n", blockLines);
                String blockMd5 = MD5Util.calculateMD5(blockAsString);



                byte[] signature = SecurityUtils.sign(SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE,
                        SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate(), blockMd5.getBytes());


                //tag
                Random rand = new Random();
                int n = rand.nextInt(blockLines.size()) + 1; // +1 是为了避开表头

                // 在第n行插入MD5值
                blockLines.add(n, "MD5:" + Arrays.toString(signature));

                // 创建元数据条目
                metadataWriter.write("MD5: " + Arrays.toString(signature));
                metadataWriter.newLine();

                // 将数据块写入最终的CSV文件
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

    // 在调用方法时传入tag:




    public static void compressFile(String filePath) throws IOException {
        String zipFile = filePath + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.putNextEntry(new ZipEntry(new File(filePath).getName()));

            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
        }

        Files.delete(Paths.get(filePath)); // 删除原始的分割文件
    }
}

