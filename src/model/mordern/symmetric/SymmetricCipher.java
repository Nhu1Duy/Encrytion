package model.mordern.symmetric;

import java.util.Base64;

/**
 * SymmetricCipher – interface chung cho tất cả giải thuật mã hóa đối xứng.
 *
 * Mọi cipher đều phải cung cấp:
 *   - genKey(int keySize)     : tạo key ngẫu nhiên với kích thước cho trước
 *   - loadKeyFromBase64(str)  : nạp key từ chuỗi Base64 (người dùng nhập sẵn)
 *   - getKeyAsBase64()        : xuất key hiện tại ra Base64 để lưu / hiển thị
 *   - getSupportedKeySizes()  : danh sách key size hỗ trợ (bit)
 *   - encryptText(plaintext)  : mã hóa chuỗi → Base64 ciphertext
 *   - decryptText(base64)     : giải mã Base64 ciphertext → plaintext
 */
public interface SymmetricCipher {

    /** Tạo key ngẫu nhiên. keySize là số bit (ví dụ 128, 192, 256). */
    void genKey(int keySize) throws Exception;

    /** Nạp key từ chuỗi Base64 (do người dùng cung cấp). */
    void loadKeyFromBase64(String base64Key) throws Exception;

    /** Xuất key hiện tại dưới dạng Base64. */
    String getKeyAsBase64();

    /** Danh sách kích thước key hợp lệ (đơn vị: bit). */
    int[] getSupportedKeySizes();

    /** Mã hóa chuỗi UTF-8, trả về Base64. */
    String encryptText(String plaintext) throws Exception;

    /** Giải mã Base64 ciphertext, trả về chuỗi UTF-8. */
    String decryptText(String base64Ciphertext) throws Exception;

    // ── Tiện ích dùng chung ──────────────────────────────────────

    static byte[] base64ToBytes(String s) {
        return Base64.getDecoder().decode(s.trim());
    }

    static String bytesToBase64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
}