package model.mordern.symmetric;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Blowfish – CBC mode, PKCS7 padding, sử dụng Bouncy Castle.
 *
 * Hỗ trợ key size: 32–448 bit (bội số 8).
 * Gợi ý: 128, 192, 256.
 * IV (8 bytes = block size Blowfish) được prepend vào output.
 *
 * Dependency: bcprov-jdk18on-*.jar (Bouncy Castle)
 */
public class Blowfish implements SymmetricCipher {

    private static final int IV_SIZE = 8; // Blowfish block = 64 bit

    private byte[] keyBytes;

    // ── SymmetricCipher ───────────────────────────────────────────

    @Override
    public void genKey(int keySize) throws Exception {
        if (keySize < 32 || keySize > 448 || keySize % 8 != 0)
            throw new IllegalArgumentException("Blowfish hỗ trợ key từ 32 đến 448 bit, bội số 8.");
        keyBytes = new byte[keySize / 8];
        new SecureRandom().nextBytes(keyBytes);
    }

    @Override
    public void loadKeyFromBase64(String base64Key) throws Exception {
        keyBytes = Base64.getDecoder().decode(base64Key.trim());
    }

    @Override
    public String getKeyAsBase64() {
        ensureKey();
        return Base64.getEncoder().encodeToString(keyBytes);
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
        byte[] data      = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = process(true, data, iv);
        return Base64.getEncoder().encodeToString(concat(iv, encrypted));
    }

    @Override
    public String decryptText(String base64Ciphertext) throws Exception {
        ensureKey();
        byte[] combined  = Base64.getDecoder().decode(base64Ciphertext.trim());
        byte[] iv        = extract(combined, 0, IV_SIZE);
        byte[] encrypted = extract(combined, IV_SIZE, combined.length - IV_SIZE);
        byte[] decrypted = process(false, encrypted, iv);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── File ─────────────────────────────────────────────────────

    public boolean encryptFile(String src, String des) throws Exception {
        ensureKey();
        byte[] iv = generateIV();

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            out.write(iv);
            byte[] plaintext = in.readAllBytes();
            byte[] encrypted = process(true, plaintext, iv);
            out.write(encrypted);
        }
        return true;
    }

    public boolean decryptFile(String src, String des) throws Exception {
        ensureKey();

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            byte[] iv        = in.readNBytes(IV_SIZE);
            byte[] ciphertext = in.readAllBytes();
            byte[] decrypted = process(false, ciphertext, iv);
            out.write(decrypted);
        }
        return true;
    }

    // ── Bouncy Castle core ────────────────────────────────────────

    private byte[] process(boolean forEncryption, byte[] input, byte[] iv) throws Exception {
        BufferedBlockCipher cipher = buildCipher(forEncryption, iv);
        byte[] output = new byte[cipher.getOutputSize(input.length)];
        int len = cipher.processBytes(input, 0, input.length, output, 0);
        len += cipher.doFinal(output, len);
        byte[] result = new byte[len];
        System.arraycopy(output, 0, result, 0, len);
        return result;
    }

    private BufferedBlockCipher buildCipher(boolean forEncryption, byte[] iv) {
        BlowfishEngine engine  = new BlowfishEngine();
        CBCBlockCipher cbc     = new CBCBlockCipher(engine);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbc, new PKCS7Padding());
        CipherParameters params = new ParametersWithIV(new KeyParameter(keyBytes), iv);
        cipher.init(forEncryption, params);
        return cipher;
    }

    // ── Internal helpers ─────────────────────────────────────────

    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private void ensureKey() {
        if (keyBytes == null) throw new IllegalStateException("Key chưa được khởi tạo.");
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
