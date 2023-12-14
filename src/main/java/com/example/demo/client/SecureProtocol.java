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

    public byte[] clientInitiatesChallenge() throws GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] rc = new byte[16];
        random.nextBytes(rc);

        byte[] encryptedRc = SecurityUtils.encrypt(serverPublicKey, rc);

        byte[] signature = SecurityUtils.sign(clientPrivateKey, rc);

        challengeMap.put(Base64.getEncoder().encodeToString(rc), rc);

        return concatenateArrays(encryptedRc, signature);
    }

    public boolean clientVerifiesResponse(byte[] response, byte[] signature) throws GeneralSecurityException {
        byte[] decryptedResponse = SecurityUtils.decrypt(clientPrivateKey, response);

        return SecurityUtils.verify(serverPublicKey, decryptedResponse, signature) && challengeMap.containsKey(Base64.getEncoder().encodeToString(decryptedResponse));
    }

    private byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
