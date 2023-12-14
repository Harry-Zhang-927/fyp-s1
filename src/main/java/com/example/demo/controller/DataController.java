package com.example.demo.controller;

import com.example.demo.client.utils.FileUtil;
import com.example.demo.client.utils.MD5Util;
import com.example.demo.service.LabelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
public class DataController {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";

    @Autowired
    LabelingService labelingService;
    @PostMapping("/labeling")
    public CompletableFuture<Boolean> handleBinaryUpload(@RequestBody byte[] binaryData) throws IOException {

        // 处理二进制数据
        FileUtil.byteArrayToFile(binaryData, outputCsvPath);

        CompletableFuture<Boolean> res =  labelingService.sendData(binaryData);

        System.out.println("[Third Party] checking if the file is consistent");
        boolean isConsistent = Objects.equals(MD5Util.calculateMD5ByFilePath(inputCsvPath), MD5Util.calculateMD5ByFilePath(outputCsvPath));
        System.out.println("[Third Party] files are consistent :::" + isConsistent);

        return res;
    }
}
