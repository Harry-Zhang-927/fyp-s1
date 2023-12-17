package com.example.demo.service;

import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.client.utils.SecurityUtils;
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
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ChallengingService {
    @Autowired
    private RestTemplate restTemplate;
    public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
    @Async
    public CompletableFuture<Boolean> requestBlock(String signature) throws IOException, GeneralSecurityException {
        String url = "http://localhost:8082/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(signature, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        PrivateKey privateKey = SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE, SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate();
        byte[] sig = SecurityUtils.sign(privateKey, Objects.requireNonNull(response.getBody()).getBytes());
        Boolean res = Arrays.toString(sig).equals(signature.substring(10));
        return CompletableFuture.completedFuture(res);
    }
}
