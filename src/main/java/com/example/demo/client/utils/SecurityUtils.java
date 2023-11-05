package com.example.demo.client.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecurityUtils {

    public static final String CLIENT_PUBLIC_KEY_FILE = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/keys/clientPublicKey.txt";
    public static final String SERVER_PUBLIC_KEY_FILE = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/keys/serverPublicKey.txt";
    public static final String CLIENT_PRIVATE_KEY_FILE = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/keys/clientPrivateKey.txt";
    public static final String SERVER_PRIVATE_KEY_FILE = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/keys/serverPrivateKey.txt";

    public static void saveKeyPair(KeyPair keyPair, String publicKeyPath, String privateKeyPath) throws IOException {
        // 保存公钥
        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        writeFile(publicKeyPath, Base64.getEncoder().encodeToString(publicKeyBytes));

        // 保存私钥
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        writeFile(privateKeyPath, Base64.getEncoder().encodeToString(privateKeyBytes));
    }

    private static void writeFile(String fileName, String data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(data);
        }
    }

    public static KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // 读取公钥
        String publicKeyData = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyData);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 读取私钥
        String privateKeyData = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyData);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    public static KeyPair getKeyPair(String publicKeyPath, String privateKeyPath) throws IOException, GeneralSecurityException {
        // 检查密钥文件是否存在
        if (new File(publicKeyPath).exists() && new File(privateKeyPath).exists()) {
            // 如果存在，读取密钥对
            return loadKeyPair(publicKeyPath, privateKeyPath);
        } else {
            // 如果不存在，生成新的密钥对
            KeyPair keyPair = generateKeyPair();
            // 并保存它们
            saveKeyPair(keyPair, publicKeyPath, privateKeyPath);
            return keyPair;
        }
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.genKeyPair();
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static SecretKey byteToSecretKey(byte[] decryptedKeyBytes) {
        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    public static byte[] encrypt(SecretKey key, byte[] data, AlgorithmParameterSpec algorithmParameterSpec) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, algorithmParameterSpec);
        return cipher.doFinal(data);
    }

    public static IvParameterSpec ivParameterSpecGenerator() {
        byte[] iv = new byte[16]; // 16 bytes for AES
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encrypt(PublicKey key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(SecretKey key, byte[] data, AlgorithmParameterSpec algorithmParameterSpec) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(PrivateKey key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] sign(PrivateKey key, byte[] data) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verify(PublicKey key, byte[] data, byte[] sig) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(key);
        signature.update(data);
        return signature.verify(sig);
    }
}
