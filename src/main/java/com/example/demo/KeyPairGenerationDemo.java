package com.example.demo;

import com.example.demo.client.utils.SecurityUtils;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPairGenerationDemo {
    public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException {
        KeyPair clientKeyPair = SecurityUtils.generateKeyPair();
        PublicKey clientPublicKey = clientKeyPair.getPublic();
        PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
        System.out.println(clientPublicKey);
        System.out.println(clientPrivateKey);
        System.out.println("[Client] Public Key and Private key generated");

        Thread.sleep(2000);

        KeyPair serverKeyPair = SecurityUtils.generateKeyPair();
        PublicKey serverPublicKey = serverKeyPair.getPublic();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
        System.out.println(serverPublicKey);
        System.out.println(serverPrivateKey);
        System.out.println("[Server] Public Key and Private key generated");
//
//
        //todo exchange the public keys
        System.out.println("[Server] get [Client] Public Key");
        System.out.println("[Client] get [Server] Public Key");

    }
}
