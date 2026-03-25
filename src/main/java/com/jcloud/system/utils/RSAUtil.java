package com.jcloud.system.utils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {
    private static final String ALGORITHM = "RSA";

    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }


    public static PublicKey stringToPublicKey(String publicKeyString) throws Exception {
        byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyString); // 使用 Base64 解码字符串
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }


    private static final String publicKeyString = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM/NDkHhESf3ZTjUkSgfC1jRH4/CEzGnvB1hme344/mnbKfk+kahbDn/Qc/e/UoZf4jNBR39/yWRlA9R9SwskYkCAwEAAQ==";

    public static String encipher(String data) {
        try {
            PublicKey publicKey = stringToPublicKey(publicKeyString);
            String encryptedData = encrypt(data, publicKey);
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void main(String[] args) {
        try {
            String data = "admin,2024-12-01,123456";
            String encryptedData = encipher(data);
            System.out.println("Encrypted Data: " + encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
