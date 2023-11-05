package com.example.demo;

import com.example.demo.client.utils.DataBlockUtil;
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
    public static final String csvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/serverDB/trf1.csv_tagged.csv";
    public static final String signatureToFind = "[-127, 93, 24, -73, 32, -127, -53, 72, 31, -39, 55, 8, -100, 11, -38, 111, 5, 65, -111, -5, 117, 8, -32, 120, 1, 98, 111, 66, 91, -37, 36, -17, -3, 17, 19, -42, 16, -5, -47, 118, 105, 104, -43, -18, 65, -97, -22, 18, 22, 35, -114, 105, -4, 45, -37, -41, -19, 40, 0, 90, 95, 58, -97, 94, -81, 1, -81, -85, 12, -39, -92, -110, 96, -46, 14, 96, -80, 16, 73, -64, -24, 96, 99, 65, 122, -80, -44, -98, -28, -100, 37, 44, 60, 107, 38, -33, 81, -10, -14, 36, 17, -58, 123, 83, 7, 43, 70, -69, -17, 96, 18, -52, -24, -111, -6, 109, -36, 73, -58, -47, 96, -25, 94, 118, 62, -33, 55, 3]";
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
        String blockMetaData = DataBlockUtil.findAndRemoveBlockWithSignature(csvPath, 1000, decryptedChallengeMsg);

        String signatureOfReturnedMetadata = Arrays.toString(SecurityUtils.sign(SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE,
                SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate(), blockMetaData.getBytes()));
        System.out.println("[Client] files are consistent ::: " + Objects.equals(signatureOfReturnedMetadata, decryptedChallengeMsg));
    }
}
