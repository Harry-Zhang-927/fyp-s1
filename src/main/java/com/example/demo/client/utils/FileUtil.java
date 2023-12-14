package com.example.demo.client.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    public static byte[] fileToByteArray(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static void byteArrayToFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }

    public static void csvShuffle() throws IOException {
        String inputFile = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1_10mil.csv"; // 输入文件路径
        String outputFile = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1_10mil_shuffled.csv"; // 输出文件路径

        int batchSize = 100000; // 每批处理的行数

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);

                if (lines.size() == batchSize) {
                    // 打乱并写入当前批次
                    Collections.shuffle(lines);
                    for (String shuffledLine : lines) {
                        bw.write(shuffledLine);
                        bw.newLine();
                    }
                    lines.clear(); // 清空列表以处理下一批
                }
            }

            // 处理剩余的行
            if (!lines.isEmpty()) {
                Collections.shuffle(lines);
                for (String shuffledLine : lines) {
                    bw.write(shuffledLine);
                    bw.newLine();
                }
            }
        }
    }


}
