package com.example.demo.controller;
import com.example.demo.model.BlockProcessingVO;
import com.example.demo.service.ChallengingService;
import com.example.demo.service.LabelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class DataController {
    public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
    public static final String outputMetaDataPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/clientDB/metaData.txt";

    @Autowired
    LabelingService labelingService;
    @Autowired
    ChallengingService challengingService;
    @PostMapping("/labelingPar")
    public List<String> csvLabelingPar(@RequestBody byte[] binaryData) throws IOException, ExecutionException, InterruptedException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        CompletableFuture<BlockProcessingVO> res = labelingService.sendDataPar(binaryData);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Par :: " + (System.currentTimeMillis() - start));
        return res.get().getSignatures();
    }
    @PostMapping("/labelingSeq")
    public List<String> csvLabelingSeq(@RequestBody byte[] binaryData) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        CompletableFuture<BlockProcessingVO> res = labelingService.sendDataSeq(binaryData);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Seq:: " + (System.currentTimeMillis() - start));
        return res.get().getSignatures();
    }

    @PostMapping("/challenging")
    public boolean dataChallenging(@RequestBody String tag) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        System.out.println("[Client] Start :: Split the File");
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> res = challengingService.requestBlock(tag);
        System.out.println("[Client] End ::  Split the File");
        System.out.println("TimeCost Seq:: " + (System.currentTimeMillis() - start));
        return res.get();
    }
}
