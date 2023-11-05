package com.example.demo.client;

import com.example.demo.DemoApplication;
import com.example.demo.client.utils.DataBlockUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Client {

    //SecureProtocol secureProtocol = new SecureProtocol();
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/testing";
    public static void main(String[] args) throws IOException {
        System.out.println("[Client] Start :: Split the File");
        DataBlockUtil.splitAndCompressCsv(inputCsvPath, 1000, outputCsvPath);
        System.out.println("[Client] End ::  Split the File");

        System.out.println("[Client] Start :: Upload the Files");
        // for tmr
        System.out.println("[Client] End ::  Upload the Files");


        System.out.println("[Client] Start :: Challenge");

        System.out.println("[Client] End ::  Challenger");





    }

}
