package model.mordern.symmetric;

import java.util.Base64;

public interface SymmetricCipher {

    void genKey(int keySize) throws Exception;

    void loadKeyFromBase64(String base64Key) throws Exception;

    String getKeyAsBase64();

    int[] getSupportedKeySizes();

    String encryptText(String plaintext) throws Exception;
    
    String decryptText(String base64Ciphertext) throws Exception;

    static byte[] base64ToBytes(String s) {
        return Base64.getDecoder().decode(s.trim());
    }
    static String bytesToBase64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
}