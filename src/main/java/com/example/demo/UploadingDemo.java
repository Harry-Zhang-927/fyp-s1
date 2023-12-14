package com.example.demo;

import com.example.demo.client.utils.DataBlockUtil;
import com.example.demo.client.utils.FileUtil;
import com.example.demo.client.utils.MD5Util;
import com.example.demo.client.utils.SecurityUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication
public class UploadingDemo {
	public static final String inputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1in.csv";
	//public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/serverDB/trf1.csv";
	public static final String outputCsvPath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.csv_tagged.csv";
	public static void main(String[] args) throws GeneralSecurityException, IOException, InterruptedException {
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

		System.out.println("[Client] encrypting the csv file");
		byte[] bytesFile = FileUtil.fileToByteArray(inputCsvPath);
		byte[] encrypted = SecurityUtils.encrypt(secretKey, bytesFile, iv);
		System.out.println("[Client] encrypted the csv file");



		System.out.println("[Server] decrypting the csv file");
		byte[] decrypted = SecurityUtils.decrypt(secretKey, encrypted, iv);
		System.out.println("[Server] encrypted the csv file");

		String filePath = "/Users/zhanghaoran/Desktop/FYP/data/data5/1/trf1.bin";


		System.out.println("[Server] downloading the csv file");
		FileUtil.byteArrayToFile(decrypted, outputCsvPath);
		System.out.println("[Server] downloaded the csv file");

		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			fos.write(decrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}




		System.out.println("[Third Party] checking if the file is consistent");
		boolean isConsistent = Objects.equals(MD5Util.calculateMD5ByFilePath(inputCsvPath), MD5Util.calculateMD5ByFilePath(outputCsvPath));
		System.out.println("[Third Party] files are consistent :::" + isConsistent);


	}


}
