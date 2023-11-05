package com.example.demo;

import com.example.demo.client.utils.DataBlockUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PreparationDemo {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/serverDB";
    public static final String outputMetaDataPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/clientDB/metaData.txt";
    public static void main(String[] args) throws IOException {
        System.out.println("[Client] Start :: Split the File");
        DataBlockUtil.splitCsvToMetadata(inputCsvPath, 1000, outputMetaDataPath, outputCsvPath);
        System.out.println("[Client] End ::  Split the File");
    }

}
