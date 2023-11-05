package com.example.demo.client;

import com.example.demo.client.utils.SecurityUtils;

import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SecureProtocol {

    private PublicKey serverPublicKey;
    private PrivateKey clientPrivateKey;
    private Map<String, byte[]> challengeMap = new HashMap<>();

    public SecureProtocol(PublicKey serverPubKey, PrivateKey clientPrivKey) {
        this.serverPublicKey = serverPubKey;
        this.clientPrivateKey = clientPrivKey;
    }

    // 客户端发起挑战
    public byte[] clientInitiatesChallenge() throws GeneralSecurityException {
        // 选择一个随机数 rc
        SecureRandom random = new SecureRandom();
        byte[] rc = new byte[16];
        random.nextBytes(rc);

        // 加密 rc
        byte[] encryptedRc = SecurityUtils.encrypt(serverPublicKey, rc);

        // 对 rc 进行签名
        byte[] signature = SecurityUtils.sign(clientPrivateKey, rc);

        // 存储挑战以备将来验证响应
        challengeMap.put(Base64.getEncoder().encodeToString(rc), rc);

        // 返回包含加密的 rc 和签名的数据包
        return concatenateArrays(encryptedRc, signature);
    }

    // 客户端验证服务器响应
    public boolean clientVerifiesResponse(byte[] response, byte[] signature) throws GeneralSecurityException {
        // 解密响应
        byte[] decryptedResponse = SecurityUtils.decrypt(clientPrivateKey, response);

        // 验证签名
        return SecurityUtils.verify(serverPublicKey, decryptedResponse, signature) && challengeMap.containsKey(Base64.getEncoder().encodeToString(decryptedResponse));
    }

    private byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
