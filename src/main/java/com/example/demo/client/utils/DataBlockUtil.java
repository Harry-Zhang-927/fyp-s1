package com.example.demo.client.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void splitAndCompressCsv(String inputCsvPath, int rowsPerFile, String outputDirPath) throws IOException {
        Path outputPath = Paths.get(outputDirPath);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputCsvPath), StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // 读取表头
            String[] headers = line != null ? line.split(",") : new String[0];
            int fileCount = 0;
            int rowCount = 0;

            while (line != null) {
                String outputFile = outputDirPath + File.separator + "output_" + fileCount + ".csv";
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
                    writer.write(String.join(",", headers));
                    writer.newLine();

                    while (rowCount < rowsPerFile && line != null) {
                        writer.write(line);
                        writer.newLine();
                        line = reader.readLine();
                        rowCount++;
                    }
                }

                //compressFile(outputFile);
                String md5 = MD5Util.calculateMD5(outputFile);
                MD5Util.saveMd5AndUuid(md5);
                rowCount = 0;
                fileCount++;
            }
            PropertiesUtil.readProperties("/Users/zhanghaoran/Desktop/FYP/data/data5/1/testing/uuid_md5/uuid_md5.properties");
            System.out.println("_______________________________________________________________________________________________");
            System.out.println("Split and Compression is done");
        }
    }

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

