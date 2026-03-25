package com.jcloud.system.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 可以选择 128、192 或 256 位密钥
        return keyGen.generateKey();
    }

    public static String encrypt(String data, String keys) throws Exception {
        SecretKey key = stringToKey(keys);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, String keys) throws Exception {
        SecretKey key = stringToKey(keys);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }


    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    public static void main(String[] args) throws Exception {
        String key = "uWLKpTnurIcJw2d8vOmspA==";
        String data = "{\n" +
                "    \"ip\": \"192.168.0.133\",\n" +
                "    \"verify\": \"你好123\"\n" +
                "}";
        String encrypt = encrypt(data,key);
        String decrypt = decrypt(encrypt,key);
        System.out.println(encrypt);
        System.out.println(decrypt);
    }
}
