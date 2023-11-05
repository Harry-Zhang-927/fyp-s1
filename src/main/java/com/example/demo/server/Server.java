package com.example.demo.server;

import java.security.*;

public class Server {
    private PublicKey clientPublicKey;
    private PrivateKey serverPrivateKey;

    public Server(PublicKey clientPubKey, PrivateKey serverPrivKey) {
        this.clientPublicKey = clientPubKey;
        this.serverPrivateKey = serverPrivKey;
    }

    public boolean verifyChallenge(byte[] challengeData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // 服务器验证签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(clientPublicKey);
        signature.update(challengeData);
        // ... 更新其他需要验证的数据

        // 验证签名是否有效
        return signature.verify(challengeData);
    }

    // ... 服务器的其他方法
}

