package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
@Service
public class LabelingService {
    @Autowired
    private RestTemplate restTemplate;

    @Async
    public CompletableFuture<Boolean> sendData(byte[] data) {
        String url = "http://localhost:8082/store";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, entity, Boolean.class);
        return CompletableFuture.completedFuture(response.getBody());
    }
}
