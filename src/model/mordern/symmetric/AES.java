package model.mordern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES – Advanced Encryption Standard, CBC mode với IV ngẫu nhiên.
 *
 * Hỗ trợ key size: 128, 192, 256 bit.
 * IV (16 bytes) được prepend vào ciphertext khi encrypt và tách ra khi decrypt.
 */
public class AES implements SymmetricCipher {

    private static final String ALGORITHM      = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int    IV_SIZE        = 16; // AES block size = 128 bit

    private SecretKey key;

    // ── SymmetricCipher ───────────────────────────────────────────

    @Override
    public void genKey(int keySize) throws Exception {
        validateKeySize(keySize);
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(keySize);
        key = kg.generateKey();
    }

    @Override
    public void loadKeyFromBase64(String base64Key) throws Exception {
        byte[] raw = Base64.getDecoder().decode(base64Key.trim());
        if (raw.length != 16 && raw.length != 24 && raw.length != 32) {
            throw new IllegalArgumentException("Key AES phải là 16, 24 hoặc 32 bytes (128/192/256 bit).");
        }
        key = new SecretKeySpec(raw, ALGORITHM);
    }

    @Override
    public String getKeyAsBase64() {
        ensureKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Override
    public int[] getSupportedKeySizes() {
        return new int[]{128, 192, 256};
    }

    // ── Text ─────────────────────────────────────────────────────

    @Override
    public String encryptText(String plaintext) throws Exception {
        ensureKey();
        byte[] iv        = generateIV();
        Cipher cipher    = buildCipher(Cipher.ENCRYPT_MODE, iv);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(concat(iv, encrypted));
    }

    @Override
    public String decryptText(String base64Ciphertext) throws Exception {
        ensureKey();
        byte[] combined  = Base64.getDecoder().decode(base64Ciphertext.trim());
        byte[] iv        = extract(combined, 0, IV_SIZE);
        byte[] encrypted = extract(combined, IV_SIZE, combined.length - IV_SIZE);
        Cipher cipher    = buildCipher(Cipher.DECRYPT_MODE, iv);
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    // ── File ─────────────────────────────────────────────────────

    /**
     * Mã hóa file src → des. 16 bytes đầu output là IV.
     */
    public boolean encryptFile(String src, String des) throws Exception {
        ensureKey();
        byte[] iv     = generateIV();
        Cipher cipher = buildCipher(Cipher.ENCRYPT_MODE, iv);

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            out.write(iv);

            try (CipherInputStream cis = new CipherInputStream(in, cipher)) {
                pipe(cis, out);
            }
        }
        return true;
    }

    /**
     * Giải mã file src → des. Đọc IV từ 16 bytes đầu.
     */
    public boolean decryptFile(String src, String des) throws Exception {
        ensureKey();

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            byte[] iv     = in.readNBytes(IV_SIZE);
            Cipher cipher = buildCipher(Cipher.DECRYPT_MODE, iv);

            try (CipherOutputStream cos = new CipherOutputStream(out, cipher)) {
                pipe(in, cos);
            }
        }
        return true;
    }

    // ── Internal helpers ─────────────────────────────────────────

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

    private void validateKeySize(int bits) {
        if (bits != 128 && bits != 192 && bits != 256)
            throw new IllegalArgumentException("AES chỉ hỗ trợ keySize 128, 192, 256 bit.");
    }

    private void ensureKey() {
        if (key == null) throw new IllegalStateException("Key chưa được khởi tạo.");
    }

    private static void pipe(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    private static byte[] extract(byte[] src, int offset, int len) {
        byte[] r = new byte[len];
        System.arraycopy(src, offset, r, 0, len);
        return r;
    }
}
