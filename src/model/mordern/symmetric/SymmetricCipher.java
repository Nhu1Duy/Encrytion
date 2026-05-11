package model.mordern.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public interface SymmetricCipher {

    void setTransformation(String mode, String padding);
    
    SecretKey genKey() throws Exception;
    
    void loadKey(SecretKey key);
    
    IvParameterSpec genIV();
    
    void loadIV(IvParameterSpec iv);

    String encryptBase64(String plainText) throws Exception;

    String decryptBase64(String encryptedText) throws Exception;

    boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception;
}