package view;

/**
 * KeyPanel – interface chuẩn hoá việc đọc/ghi khóa trên mọi cipher panel.
 *
 * Controller dùng interface này để import/save key mà không cần biết
 * từng panel lưu khóa theo cấu trúc gì (JTextField, JTextArea, hai field, ...).
 *
 * Định dạng chuỗi khoá theo từng cipher:
 *   Caesar       →  "3"
 *   Affine       →  "7,3"         (a,b)
 *   Vigenere     →  "LEMON"
 *   Substitution →  chuỗi hoán vị đầy đủ của alphabet
 *   Permutation  →  "2 0 3 1"
 *   Hill         →  chuỗi nội bộ của HillCipher.matrixToKey()
 */
public interface KeyPanel {

    /**
     * Trả về khóa hiện tại dưới dạng String.
     * Dùng để lưu ra file hoặc đọc vào controller.
     */
    String getKeyText();

    /**
     * Nạp khóa từ String (ví dụ: đọc từ file) vào các field tương ứng.
     */
    void setKeyText(String key);
}