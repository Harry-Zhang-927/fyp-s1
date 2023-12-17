package com.example.demo.service;

import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.model.BlockProcessingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;
@Service
public class LabelingService {
    @Autowired
    private RestTemplate restTemplate;
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
    @Async
    public CompletableFuture<BlockProcessingVO> sendDataPar(byte[] data) {
        String url = "http://localhost:8082/store";

        BlockProcessingVO res = DataBlockUtil.splitCsvToMetadataPar(data, 1000);

        return getBlockProcessingVOCompletableFuture(url, res);
    }

    @Async
    public CompletableFuture<BlockProcessingVO> sendDataSeq(byte[] data) throws IOException, GeneralSecurityException {
        String url = "http://localhost:8082/store";

        BlockProcessingVO res = DataBlockUtil.splitCsvToMetadataSequential(data, 1000);

        return getBlockProcessingVOCompletableFuture(url, res);
    }

    private CompletableFuture<BlockProcessingVO> getBlockProcessingVOCompletableFuture(String url, BlockProcessingVO res) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> entity = new HttpEntity<>(res.getCsvBin(), headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, entity, Boolean.class);
        res.setStoredSuccessfully(response.getBody());
        return CompletableFuture.completedFuture(res);
    }
}
