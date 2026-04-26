package model.mordern.symmetric;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * RC4 – Rivest Cipher 4, stream cipher tự implement.
 *
 * Lưu ý: RC4 đã bị loại khỏi nhiều chuẩn bảo mật (TLS 1.3 không hỗ trợ).
 * Chỉ dùng cho mục đích học tập.
 *
 * Vì RC4 là stream cipher, không có IV. Để tránh tái sử dụng keystream
 * (key-reuse attack), mỗi lần encrypt sẽ sinh thêm 16-byte nonce ngẫu nhiên
 * và kết hợp nonce vào đầu key trước khi init KSA.
 *
 * Định dạng output: [16-byte nonce][ciphertext] → Base64.
 */
public class RC4 implements SymmetricCipher {

    private static final int NONCE_SIZE = 16;

    private byte[] keyBytes;

    // ── SymmetricCipher ───────────────────────────────────────────

    @Override
    public void genKey(int keySize) throws Exception {
        if (keySize < 40 || keySize > 2048 || keySize % 8 != 0)
            throw new IllegalArgumentException("RC4 hỗ trợ key từ 40 đến 2048 bit, bội số 8.");
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
        // Trả về các giá trị gợi ý; thực tế hỗ trợ 40–2048
        return new int[]{40, 128, 256};
    }

    // ── Text ─────────────────────────────────────────────────────

    @Override
    public String encryptText(String plaintext) throws Exception {
        ensureKey();
        byte[] nonce     = generateNonce();
        byte[] data      = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = rc4(data, buildEffectiveKey(nonce));
        return Base64.getEncoder().encodeToString(concat(nonce, encrypted));
    }

    @Override
    public String decryptText(String base64Ciphertext) throws Exception {
        ensureKey();
        byte[] combined  = Base64.getDecoder().decode(base64Ciphertext.trim());
        byte[] nonce     = extract(combined, 0, NONCE_SIZE);
        byte[] encrypted = extract(combined, NONCE_SIZE, combined.length - NONCE_SIZE);
        byte[] decrypted = rc4(encrypted, buildEffectiveKey(nonce));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── File ─────────────────────────────────────────────────────

    /**
     * Mã hóa file src → des.
     * Định dạng: [16-byte nonce][RC4(plaintext)].
     */
    public boolean encryptFile(String src, String des) throws Exception {
        ensureKey();
        byte[] nonce        = generateNonce();
        byte[] effectiveKey = buildEffectiveKey(nonce);
        int[]  S            = ksaInit(effectiveKey);
        int[]  ij           = {0, 0}; // i, j state

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            out.write(nonce);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                byte[] block = new byte[len];
                System.arraycopy(buf, 0, block, 0, len);
                xorWithKeystream(block, S, ij);
                out.write(block);
            }
        }
        return true;
    }

    /**
     * Giải mã file src → des (RC4 encrypt = decrypt).
     */
    public boolean decryptFile(String src, String des) throws Exception {
        ensureKey();

        try (BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des))) {

            byte[] nonce        = in.readNBytes(NONCE_SIZE);
            byte[] effectiveKey = buildEffectiveKey(nonce);
            int[]  S            = ksaInit(effectiveKey);
            int[]  ij           = {0, 0};

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                byte[] block = new byte[len];
                System.arraycopy(buf, 0, block, 0, len);
                xorWithKeystream(block, S, ij);
                out.write(block);
            }
        }
        return true;
    }

    // ── RC4 core ─────────────────────────────────────────────────

    /**
     * RC4 full: KSA + PRGA trên toàn bộ data.
     */
    private byte[] rc4(byte[] data, byte[] k) {
        int[] S = ksaInit(k);
        int[] ij = {0, 0};
        byte[] out = data.clone();
        xorWithKeystream(out, S, ij);
        return out;
    }

    /**
     * KSA (Key Scheduling Algorithm) — khởi tạo S-box.
     */
    private int[] ksaInit(byte[] k) {
        int[] S = new int[256];
        for (int i = 0; i < 256; i++) S[i] = i;
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + (k[i % k.length] & 0xFF)) & 0xFF;
            swap(S, i, j);
        }
        return S;
    }

    /**
     * PRGA (Pseudo-Random Generation Algorithm) — XOR keystream vào data (in-place).
     * ij[0] = i, ij[1] = j — giữ state qua nhiều lần gọi (dùng cho streaming file).
     */
    private void xorWithKeystream(byte[] data, int[] S, int[] ij) {
        int i = ij[0], j = ij[1];
        for (int k = 0; k < data.length; k++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            swap(S, i, j);
            data[k] ^= (byte) S[(S[i] + S[j]) & 0xFF];
        }
        ij[0] = i;
        ij[1] = j;
    }

    private static void swap(int[] S, int a, int b) {
        int t = S[a]; S[a] = S[b]; S[b] = t;
    }

    // ── Internal helpers ─────────────────────────────────────────

    /** Kết hợp nonce + key để tạo effective key, tránh reuse keystream. */
    private byte[] buildEffectiveKey(byte[] nonce) {
        byte[] ek = new byte[nonce.length + keyBytes.length];
        System.arraycopy(nonce,    0, ek, 0,             nonce.length);
        System.arraycopy(keyBytes, 0, ek, nonce.length,  keyBytes.length);
        return ek;
    }

    private byte[] generateNonce() {
        byte[] n = new byte[NONCE_SIZE];
        new SecureRandom().nextBytes(n);
        return n;
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
