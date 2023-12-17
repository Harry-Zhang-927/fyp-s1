package com.example.demo.controller;
import com.example.demo.context.RequestContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class AuthenticationController {
    @GetMapping("/connect")
    public String connect(@RequestParam(value = "p") Long q, @RequestParam(value = "a") Long a) {
        if (q == null || a == null) {
            throw new IllegalArgumentException();
        }
        return "ok";
    }

    @GetMapping("/requestPK")
    public String generatePK(@RequestParam(value = "clintPublicKey") Long  clintPublicKey) {
        if (clintPublicKey == null) {
            throw new IllegalArgumentException();
        }
        String q = (String) RequestContext.get("q");
        String a = (String) RequestContext.get("a");
        Long Q = Long.valueOf(q);
        Long A = Long.valueOf(a);
        Random rand = new Random();
        Long privteKey = (long)rand.nextInt(10);
        System.out.println("my pk is: " + privteKey);
        Long publicKey =  (int) Math.pow(A, privteKey) % Q;
        return "your pk is : " + clintPublicKey + "my pk is :" + publicKey;
    }
}
