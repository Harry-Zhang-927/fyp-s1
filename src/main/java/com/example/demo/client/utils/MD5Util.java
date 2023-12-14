package com.example.demo.client.utils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;

public class MD5Util {
    public static final String PropertiesFilePath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/testing/uuid_md5/uuid_md5.properties";
    public static String calculateMD5ByFilePath(String filePath) {
        try (InputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");

            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md5Digest.update(buffer, 0, bytesRead);
            }

            byte[] digestBytes = md5Digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
