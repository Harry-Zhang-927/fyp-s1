package com.example.demo.controller;

import com.example.demo.client.utils.FileUtil;
import com.example.demo.client.utils.MD5Util;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
public class DataController {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";

    @PostMapping("/labeling")
    public String handleBinaryUpload(@RequestBody byte[] binaryData) throws IOException {

        // 处理二进制数据
        System.out.println("[Server] downloading the csv file");
        FileUtil.byteArrayToFile(binaryData, outputCsvPath);
        System.out.println("[Server] downloaded the csv file");


        System.out.println("[Third Party] checking if the file is consistent");
        boolean isConsistent = Objects.equals(MD5Util.calculateMD5ByFilePath(inputCsvPath), MD5Util.calculateMD5ByFilePath(outputCsvPath));
        System.out.println("[Third Party] files are consistent :::" + isConsistent);

        return "haha";
    }
}
