package model.clasic;

public interface ClassicCipher {
    String encryptEN(String plainText, String key);
    String decryptEN(String cipherText, String key);
 
    String encryptVN(String plainText, String key);
    String decryptVN(String cipherText, String key);
}
