package com.example.demo;

import com.example.demo.client.utils.FileUtil;
import com.example.demo.client.utils.MD5Util;
import com.example.demo.client.utils.SecurityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

@SpringBootApplication
public class DemoApplication {
	public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv";
	public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/serverDB/trf1.csv";

	public static void main(String[] args) throws GeneralSecurityException, IOException, InterruptedException {
//		KeyPair clientKeyPair = SecurityUtils.generateKeyPair();
//		PublicKey clientPublicKey = clientKeyPair.getPublic();
//		PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
//		System.out.println(clientPublicKey);
//		System.out.println(clientPrivateKey);
//		System.out.println("[Client] Public Key and Private key generated");

		Thread.sleep(2000);

//		KeyPair serverKeyPair = SecurityUtils.generateKeyPair();
//		PublicKey serverPublicKey = serverKeyPair.getPublic();
//		PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
//		System.out.println(serverPublicKey);
//		System.out.println(serverPrivateKey);
//		System.out.println("[Server] Public Key and Private key generated");
//
//
//		//System.out.println("[Server] get [Client] Public Key");
//		System.out.println("[Client] get [Server] Public Key");



		System.out.println("[Client] generating the SecretKey");
		SecretKey secretKey = SecurityUtils.generateSecretKey();
		System.out.println("[Client] generated the SecretKey");

		AlgorithmParameterSpec iv = SecurityUtils.ivParameterSpecGenerator();



		System.out.println("[Client] encrypting the csv file");
		byte[] bytesFile = FileUtil.fileToByteArray(inputCsvPath);
		byte[] encrypted = SecurityUtils.encrypt(secretKey, bytesFile, iv);
		System.out.println("[Client] encrypted the csv file");



		System.out.println("[Server] decrypting the csv file");
		byte[] decrypted = SecurityUtils.decrypt(secretKey, encrypted, iv);
		System.out.println("[Server] encrypted the csv file");

		System.out.println("[Server] downloading the csv file");
		FileUtil.byteArrayToFile(decrypted, outputCsvPath);
		System.out.println("[Server] downloaded the csv file");


		System.out.println("[Third Party] checking if the file is damaged");
		boolean isConsistent = Objects.equals(MD5Util.calculateMD5(inputCsvPath), MD5Util.calculateMD5(outputCsvPath));
		System.out.println("[Third Party] files are consistent :::" + isConsistent);













	}


}
