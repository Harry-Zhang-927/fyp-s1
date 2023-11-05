package com.example.demo.client.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static byte[] fileToByteArray(String filePath) throws IOException {
        // 使用Files.readAllBytes来快速读取文件到byte数组
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static void byteArrayToFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }
}
