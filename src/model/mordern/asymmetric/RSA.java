package model.mordern.asymmetric;

import java.security.KeyPair;
import java.util.Base64;

import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSA {
/// Trừ file thì kiến trúc OK -- RSA -- Băm -- xong 
/// 
	KeyPair keyPair;
    PrivateKey privateKey;
    PublicKey publicKey;

    public String encryptBase64(String data) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(data));
    }

    public byte[] encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        byte in[] = data.getBytes(StandardCharsets.UTF_8);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] out = cipher.doFinal(in);
        return out;
    }

    public String deCrypt(String base64) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        byte in[] = Base64.getDecoder().decode(base64);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] out = cipher.doFinal(in);
        return new String(out, StandardCharsets.UTF_8);
    }

    public void genKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

}
