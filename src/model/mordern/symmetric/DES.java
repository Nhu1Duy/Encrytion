package model.mordern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class DES implements SymmetricCipher {

    private static final String ALGORITHM    = "DES";
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final int    IV_SIZE      = 8; // bytes

    private SecretKey key;

    @Override
    public void genKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(56);
        key = kg.generateKey();
    }

    @Override
    public void loadKeyFromBase64(String base64Key) throws Exception {
        byte[] raw = Base64.getDecoder().decode(base64Key.trim());
        key = new SecretKeySpec(raw, ALGORITHM);
    }

    @Override
    public String getKeyAsBase64() {
        if (key == null) throw new IllegalStateException("Key chưa được khởi tạo.");
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Override
    public int[] getSupportedKeySizes() {
        return new int[]{56};
    }

    @Override
    public String encryptText(String plaintext) throws Exception {
        ensureKey();
        byte[] iv = generateIV();
        Cipher cipher = buildCipher(Cipher.ENCRYPT_MODE, iv);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = concat(iv, encrypted);
        return Base64.getEncoder().encodeToString(combined);
    }

    @Override
    public String decryptText(String base64Ciphertext) throws Exception {
        ensureKey();
        byte[] combined = Base64.getDecoder().decode(base64Ciphertext.trim());
        byte[] iv        = extract(combined, 0, IV_SIZE);
        byte[] encrypted = extract(combined, IV_SIZE, combined.length - IV_SIZE);
        Cipher cipher = buildCipher(Cipher.DECRYPT_MODE, iv);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

 
    public boolean encryptFile(String src, String des) throws Exception {
        ensureKey();
        byte[] iv = generateIV();
        Cipher cipher = buildCipher(Cipher.ENCRYPT_MODE, iv);

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            out.write(iv); 

            try (CipherInputStream cis = new CipherInputStream(in, cipher)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = cis.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        }
        return true;
    }

    public boolean decryptFile(String src, String des) throws Exception {
        ensureKey();

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            byte[] iv = in.readNBytes(IV_SIZE);
            Cipher cipher = buildCipher(Cipher.DECRYPT_MODE, iv);

            try (CipherOutputStream cos = new CipherOutputStream(out, cipher)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    cos.write(buf, 0, len);
                }
            }
        }
        return true;
    }

    private Cipher buildCipher(int mode, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, key, new IvParameterSpec(iv));
        return cipher;
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private void ensureKey() {
        if (key == null) throw new IllegalStateException("Key chưa được khởi tạo. Gọi genKey() hoặc loadKeyFromBase64() trước.");
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static byte[] extract(byte[] src, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, offset, result, 0, length);
        return result;
    }
}
