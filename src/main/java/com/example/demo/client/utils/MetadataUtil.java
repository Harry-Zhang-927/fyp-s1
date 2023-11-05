package com.example.demo.client.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MetadataUtil {

    public static String generateCheckMetadata(byte[] dataBlock) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataBlock);
        return Base64.getEncoder().encodeToString(hash);
    }
}
