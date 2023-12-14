package com.example.demo;

import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.client.utils.MD5Util;
import com.example.demo.client.utils.SecurityUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Objects;

public class VerificationDemo {
    public static final String csvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/serverDB/trf1_10mil_shuffled.csv_tagged.csv";
    public static final String signatureToFind = "[87, 60, 16, 30, 0, -18, -68, 80, 3, 89, -16, 24, -100, -105, -27, 74, 56, 86, -112, 55, 29, -16, -52, 126, 119, -25, -64, -32, 113, -112, -43, -22, 101, -41, 101, -37, 42, 41, -41, -116, -65, -41, 63, 105, -55, -29, -16, 70, -7, -19, -92, 23, 15, -16, -104, -22, -87, -1, -83, -36, 104, -82, -12, 112, 69, -14, 110, 75, -9, -48, -19, 35, 27, 33, -107, -44, -108, 118, -16, 93, -89, -37, -15, 21, -114, 90, 53, 34, 98, 76, -121, -117, 28, 4, 40, 74, 98, 88, 37, -56, -25, -104, 84, -58, 82, -3, 104, -48, -115, 25, 79, 53, 43, 124, -78, 83, 46, -118, 104, -63, 78, 50, 67, 99, -85, 28, 114, -60]";
    public static void main(String[] arg) throws IOException, GeneralSecurityException, InterruptedException {

        KeyPair clientKeyPair = SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE, SecurityUtils.CLIENT_PRIVATE_KEY_FILE);
        PublicKey clientPublicKey = clientKeyPair.getPublic();
        PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
        System.out.println(clientPublicKey);
        System.out.println(clientPrivateKey);
        System.out.println("[Client] Public Key and Private key achieved");

        Thread.sleep(2000);

        KeyPair serverKeyPair = SecurityUtils.getKeyPair(SecurityUtils.SERVER_PUBLIC_KEY_FILE, SecurityUtils.SERVER_PRIVATE_KEY_FILE);
        PublicKey serverPublicKey = serverKeyPair.getPublic();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
        System.out.println(serverPublicKey);
        System.out.println(serverPrivateKey);
        System.out.println("[Server] Public Key and Private key achieved");
//
//
        //todo exchange the public keys
        System.out.println("[Server] get [Client] Public Key");
        System.out.println("[Client] get [Server] Public Key");



        System.out.println("[Client] generating the SecretKey");
        SecretKey secretKey = SecurityUtils.generateSecretKey();
        System.out.println("[Client] generated the SecretKey");

        System.out.println("[Client] encrypting the SecretKey");
        byte[] encryptedSecretKey = SecurityUtils.encrypt(serverPublicKey, secretKey.getEncoded());
        System.out.println("[Client] encrypted the SecretKey");

        System.out.println("[Client] sending the encryptedSecretKey");
        //todo send the encrypted secret key to server
        System.out.println("[Clinet] sent the encryptedSecretKey");

        System.out.println("[Server] received the encryptedSecretKey");
        System.out.println("[Server] decrypting the encryptedSecretKey");
        SecretKey decryptedSecretKey = SecurityUtils.byteToSecretKey(SecurityUtils.decrypt(serverPrivateKey, encryptedSecretKey));

        System.out.println("The secretKey is consistent ::: " + Arrays.equals(decryptedSecretKey.getEncoded(), secretKey.getEncoded()));

        AlgorithmParameterSpec iv = SecurityUtils.ivParameterSpecGenerator();


        System.out.println("[Client] encrypting the challengeMsg");
        byte[] encryptedChallengeMsg = SecurityUtils.encrypt(secretKey, signatureToFind.getBytes(StandardCharsets.UTF_8), iv);
        System.out.println("[Client] encrypted the challengeMsg");
        //SecurityUtils.verify(serverPublicKey, decryptedResponse, signature)
        System.out.println("[Client] Sending the challengeMsg");
        System.out.println("[Server] Received the challengeMsg");

        System.out.println("[Client] decrypting the challengeMsg");
        String decryptedChallengeMsg = new String(SecurityUtils.decrypt(secretKey, encryptedChallengeMsg, iv), StandardCharsets.UTF_8);
        System.out.println("[Client] decrypted the challengeMsg");

        System.out.println("[Third Party] the challenge msg is consistent :: " + Objects.equals(decryptedChallengeMsg, signatureToFind));


        System.out.println("Sending the corresponding data block to client");
        System.out.println("Encrypting and decrypting will be skipped");
        String blockMetaData = DataBlockUtil.findAndRemoveBlockWithSignatureParallel(csvPath, 500000, decryptedChallengeMsg);

        String signatureOfReturnedMetadata = Arrays.toString(SecurityUtils.sign(SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE,
                SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate(), blockMetaData.getBytes()));
        System.out.println("[Client] files are consistent ::: " + Objects.equals(signatureOfReturnedMetadata, decryptedChallengeMsg));
    }
}
