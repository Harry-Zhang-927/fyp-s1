package com.example.demo.client.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static void readProperties(String propertiesFilePath) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            // 打开.properties文件的输入流
            input = new FileInputStream(propertiesFilePath);

            // 加载文件
            prop.load(input);

            // 获取所有的键值对
            prop.forEach((key, value) -> System.out.println("Key : " + key + ", Value : " + value));

            // 如果您知道某个特定的键，您可以直接获取其值
            // String value = prop.getProperty("someKey");
            // System.out.println("Value : " + value);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close(); // 关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
