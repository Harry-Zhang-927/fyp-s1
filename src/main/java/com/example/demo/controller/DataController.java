package com.example.demo.controller;
import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.service.LabelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
public class DataController {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
    public static final String outputMetaDataPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/clientDB/metaData.txt";

    @Autowired
    LabelingService labelingService;
    @PostMapping("/labelingPar")
    public List<String> csvLabelingPar(@RequestBody byte[] binaryData) throws IOException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        List<String> res = DataBlockUtil.splitCsvToMetadataPar(binaryData, 1000, outputCsvPath);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Par :: " + (System.currentTimeMillis() - start));
        return res;
    }
    @PostMapping("/labelingSeq")
    public List<String> csvLabelingSeq(@RequestBody byte[] binaryData) throws IOException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        List<String> res = DataBlockUtil.splitCsvToMetadataSequential(binaryData, 1000, outputCsvPath);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Seq:: " + (System.currentTimeMillis() - start));

        return res;
    }
}
