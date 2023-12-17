package com.example.demo.controller;
import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.model.BlockProcessingVO;
import com.example.demo.service.LabelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
public class DataController {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
    public static final String outputMetaDataPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/clientDB/metaData.txt";

    @Autowired
    LabelingService labelingService;
    @PostMapping("/labelingPar")
    public BlockProcessingVO csvLabelingPar(@RequestBody byte[] binaryData) throws IOException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        BlockProcessingVO res = DataBlockUtil.splitCsvToMetadataPar(binaryData, 1000, outputCsvPath);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Par :: " + (System.currentTimeMillis() - start));
        return res;
    }
    @PostMapping("/labelingSeq")
    public BlockProcessingVO csvLabelingSeq(@RequestBody byte[] binaryData) throws IOException, GeneralSecurityException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        BlockProcessingVO res = DataBlockUtil.splitCsvToMetadataSequential(binaryData, 1000);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Seq:: " + (System.currentTimeMillis() - start));
        return res;
    }
}
