package controller;

import model.clasic.*;
import view.MainFrame;
import view.KeyPanel;
import Tool.Alphabet;
import util.FileManager;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * CryptoController – kết nối View và Model theo mô hình MVC.
 *
 * Method ID (dùng trong CardLayout và switch-case):
 *   "Caesar"       – Dịch Chuyển
 *   "Substitution" – Thay Thế
 *   "Affine"       – Affine
 *   "Vigenere"     – Vigenere
 *   "Hill"         – Hill
 *   "Permutation"  – Hoán Vị
 *
 * Định dạng file key (.key):
 *   Caesar:       "3"
 *   Affine:       "7,3"
 *   Vigenere:     "LEMON"
 *   Substitution: chuỗi hoán vị đầy đủ
 *   Permutation:  "2 0 3 1"
 *   Hill:         "matrix=<matrixToKey()>\norigLen=<n>"
 */
public class CryptoController {

    // ── Constants ────────────────────────────────────────────────
    private static final String METHOD_CAESAR       = "Caesar";
    private static final String METHOD_SUBSTITUTION = "Substitution";
    private static final String METHOD_AFFINE       = "Affine";
    private static final String METHOD_VIGENERE     = "Vigenere";
    private static final String METHOD_HILL         = "Hill";
    private static final String METHOD_PERMUTATION  = "Permutation";

    private static final String LANG_VN = "VN";
    private static final String LANG_EN = "EN";

    // Prefix dùng trong file .key của Hill
    private static final String HILL_MATRIX_PREFIX   = "matrix=";
    private static final String HILL_ORIG_LEN_PREFIX = "origLen=";

    // ── Fields ───────────────────────────────────────────────────
    private final MainFrame view;

    private final CaesarCipher       caesarCipher       = new CaesarCipher();
    private final SubstitutionCipher  substitutionCipher = new SubstitutionCipher();
    private final VigenereCipher      vigenereCipher     = new VigenereCipher();
    private final AffineCipher        affineCipher       = new AffineCipher();
    private final HillCipher          hillCipher         = new HillCipher();
    private final PermutationCipher   permutationCipher  = new PermutationCipher();

    private String currentMethod   = METHOD_CAESAR;
    private String currentLanguage = LANG_VN;

    // Hill: lưu key matrix và độ dài gốc để decrypt đúng
    private int[][] hillKeyMatrix   = null;
    private int     hillOriginalLen = -1;

    // ── Constructor ──────────────────────────────────────────────
    public CryptoController(MainFrame view) {
        this.view = view;
        bindMenuListeners();
        bindActionListeners();
        bindGenKeyListeners();
        bindFileListeners();
    }

    // ────────────────────────────────────────────────────────────
    //  BIND LISTENERS
    // ────────────────────────────────────────────────────────────

    private void bindMenuListeners() {
        view.getItemCaesar()      .addActionListener(e -> switchMethod(METHOD_CAESAR));
        view.getItemSubstitution().addActionListener(e -> switchMethod(METHOD_SUBSTITUTION));
        view.getItemAffine()      .addActionListener(e -> switchMethod(METHOD_AFFINE));
        view.getItemVigenere()    .addActionListener(e -> switchMethod(METHOD_VIGENERE));
        view.getItemHill()        .addActionListener(e -> switchMethod(METHOD_HILL));
        view.getItemPermutation() .addActionListener(e -> switchMethod(METHOD_PERMUTATION));

        view.getItemVN().addActionListener(e -> {
            currentLanguage = LANG_VN;
            view.setLanguageStatus(LANG_VN);
        });
        view.getItemEN().addActionListener(e -> {
            currentLanguage = LANG_EN;
            view.setLanguageStatus(LANG_EN);
        });
    }

    private void bindActionListeners() {
        view.getEncryptBtn().addActionListener(e -> handleAction(true));
        view.getDecryptBtn().addActionListener(e -> handleAction(false));
    }

    private void bindGenKeyListeners() {
        // Caesar
        view.getCaesarPanel().getGenBtn().addActionListener(e -> {
            try {
                String maxStr = view.getCaesarPanel().getTextKeyLenField().trim();
                int max = maxStr.isEmpty() ? alphabetSize() : Integer.parseInt(maxStr);
                view.getCaesarPanel().setKeyField(String.valueOf(caesarCipher.genKey(max)));
            } catch (NumberFormatException ex) {
                showError("Giới hạn phải là số nguyên dương!");
            }
        });

        // Substitution
        view.getSubstitutionPanel().getGenBtn().addActionListener(e ->
                view.getSubstitutionPanel().getKeyArea().setText(
                        substitutionCipher.genKey(currentAlphabet())));

        // Affine
        view.getAffinePanel().getGenBtn().addActionListener(e -> {
            int[] keys = affineCipher.genKey(alphabetSize());
            view.getAffinePanel().getKeyA().setText(String.valueOf(keys[0]));
            view.getAffinePanel().getKeyB().setText(String.valueOf(keys[1]));
        });

        // Vigenere
        view.getVigenerePanel().getGenBtn().addActionListener(e -> {
            try {
                String lenStr = view.getVigenerePanel().getTextKeyLenField().trim();
                int length = lenStr.isEmpty() ? 8 : Integer.parseInt(lenStr);
                view.getVigenerePanel().getKeyField().setText(
                        vigenereCipher.genKey(currentAlphabet(), length));
            } catch (NumberFormatException ex) {
                showError("Độ dài khóa phải là số nguyên dương!");
            }
        });

        // Hill
        view.getHillPanel().getGenBtn().addActionListener(e -> {
            try {
                String sizeStr = view.getHillPanel().getSizeField().trim();
                int n = sizeStr.isEmpty() ? 2 : Integer.parseInt(sizeStr);
                if (n < 2 || n > 5) throw new IllegalArgumentException("Kích thước ma trận phải từ 2 đến 5!");

                hillKeyMatrix   = isVN() ? hillCipher.generateKeyVN(n) : hillCipher.generateKeyEN(n);
                hillOriginalLen = -1;

                view.getHillPanel().setKeyDisplay(hillCipher.matrixToKey(hillKeyMatrix));
            } catch (NumberFormatException ex) {
                showError("Kích thước ma trận phải là số nguyên!");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        // Permutation
        view.getPermutationPanel().getGenBtn().addActionListener(e -> {
            try {
                String lenStr = view.getPermutationPanel().getLenField().getText().trim();
                int length = lenStr.isEmpty() ? 5 : Integer.parseInt(lenStr);
                view.getPermutationPanel().getKeyField().setText(permutationCipher.genKey(length));
            } catch (NumberFormatException ex) {
                showError("Độ dài khóa hoán vị phải là số nguyên dương!");
            }
        });
    }

    // ── File listeners ───────────────────────────────────────────
    private void bindFileListeners() {

        // Import Input (.txt → inputArea)
        view.getItemImportInput().addActionListener(e -> {
            String content = FileManager.importText(view.frame);
            if (content != null) {
                view.getInputArea().setText(content);
            }
        });

        // Save Output (outputArea → .txt)
        view.getItemSaveOutput().addActionListener(e -> {
            String content = view.getOutputArea().getText();
            if (content.isBlank()) {
                showError("Chưa có kết quả để lưu!");
                return;
            }
            FileManager.saveText(view.frame, content);
        });

        // Import Key (.key / .txt → cipher panel hiện tại)
        view.getItemImportKey().addActionListener(e -> {
            String raw = FileManager.importKey(view.frame);
            if (raw == null) return;
            importKeyToCurrentPanel(raw.trim());
        });

        // Save Key (cipher panel hiện tại → .key)
        view.getItemSaveKey().addActionListener(e -> {
            String keyContent = buildKeyFileContent();
            if (keyContent == null || keyContent.isBlank()) {
                showError("Chưa có khóa để lưu!\nHãy nhập hoặc Gen Key trước.");
                return;
            }
            FileManager.saveKey(view.frame, keyContent);
        });

        // Clear All
        view.getItemClearAll().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view.frame,
                    "Xóa toàn bộ Input, Output và Key hiện tại?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.getInputArea().setText("");
                view.getOutputArea().setText("");
                clearCurrentPanelKey();
            }
        });
    }

    // ────────────────────────────────────────────────────────────
    //  SWITCH METHOD
    // ────────────────────────────────────────────────────────────

    private void switchMethod(String methodId) {
        currentMethod = methodId;
        view.showLayout(methodId);
    }

    // ────────────────────────────────────────────────────────────
    //  ENCRYPT / DECRYPT
    // ────────────────────────────────────────────────────────────

    private void handleAction(boolean isEncrypt) {
        String input = view.getInputArea().getText().trim();
        if (input.isEmpty()) {
            showError("Vui lòng nhập văn bản!");
            return;
        }
        try {
            String result = switch (currentMethod) {
                case METHOD_CAESAR       -> handleCaesar(input, isEncrypt);
                case METHOD_SUBSTITUTION -> handleSubstitution(input, isEncrypt);
                case METHOD_AFFINE       -> handleAffine(input, isEncrypt);
                case METHOD_VIGENERE     -> handleVigenere(input, isEncrypt);
                case METHOD_HILL         -> handleHill(input, isEncrypt);
                case METHOD_PERMUTATION  -> handlePermutation(input, isEncrypt);
                default -> throw new IllegalStateException("Phương pháp không hợp lệ: " + currentMethod);
            };
            view.getOutputArea().setText(result);
        } catch (NumberFormatException ex) {
            showError("Khóa phải là số hợp lệ!");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ── Cipher handlers ──────────────────────────────────────────

    private String handleCaesar(String input, boolean isEncrypt) throws Exception {
        String keyStr = view.getCaesarPanel().getTextKeyField().trim();
        if (keyStr.isEmpty()) throw new Exception("Vui lòng nhập khóa hoặc nhấn 'Gen Key' để tạo!");
        return isEncrypt
                ? (isVN() ? caesarCipher.encryptVN(input, keyStr) : caesarCipher.encryptEN(input, keyStr))
                : (isVN() ? caesarCipher.decryptVN(input, keyStr) : caesarCipher.decryptEN(input, keyStr));
    }

    private String handleSubstitution(String input, boolean isEncrypt) throws Exception {
        String key = view.getSubstitutionPanel().getKeyArea().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa không được để trống!");
        validateSubstitutionKey(key);
        return isEncrypt
                ? (isVN() ? substitutionCipher.encryptVN(input, key) : substitutionCipher.encryptEN(input, key))
                : (isVN() ? substitutionCipher.decryptVN(input, key) : substitutionCipher.decryptEN(input, key));
    }

    private String handleAffine(String input, boolean isEncrypt) throws Exception {
        String aStr = view.getAffinePanel().getTextKeyA().trim();
        String bStr = view.getAffinePanel().getTextKeyB().trim();
        if (aStr.isEmpty() || bStr.isEmpty()) throw new Exception("Cả a và b không được để trống!");
        int a = Integer.parseInt(aStr);
        int b = Integer.parseInt(bStr);
        validateAffineKey(a, b);
        String key = a + "," + b;
        return isEncrypt
                ? (isVN() ? affineCipher.encryptVN(input, key) : affineCipher.encryptEN(input, key))
                : (isVN() ? affineCipher.decryptVN(input, key) : affineCipher.decryptEN(input, key));
    }

    private String handleVigenere(String input, boolean isEncrypt) throws Exception {
        String key = view.getVigenerePanel().getKeyField().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa không được để trống!");
        return isEncrypt
                ? (isVN() ? vigenereCipher.encryptVN(input, key) : vigenereCipher.encryptEN(input, key))
                : (isVN() ? vigenereCipher.decryptVN(input, key) : vigenereCipher.decryptEN(input, key));
    }

    private String handleHill(String input, boolean isEncrypt) throws Exception {
        if (hillKeyMatrix == null)
            throw new Exception("Vui lòng nhấn 'Gen Key' để tạo khóa Hill trước!");
        if (isEncrypt) {
            String alpha = currentAlphabet();
            hillOriginalLen = (int) input.codePoints()
                    .filter(cp -> alpha.contains(new String(Character.toChars(cp))))
                    .count();
            return isVN()
                    ? hillCipher.encryptVN(input, hillKeyMatrix)
                    : hillCipher.encryptEN(input, hillKeyMatrix);
        } else {
            if (hillOriginalLen < 0)
                throw new Exception("Hãy mã hóa văn bản trước rồi mới giải mã (cần biết độ dài gốc)!");
            return isVN()
                    ? hillCipher.decryptVN(input, hillKeyMatrix, hillOriginalLen)
                    : hillCipher.decryptEN(input, hillKeyMatrix, hillOriginalLen);
        }
    }

    private String handlePermutation(String input, boolean isEncrypt) throws Exception {
        String key = view.getPermutationPanel().getKeyField().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa hoán vị không được để trống!");
        return isEncrypt
                ? (isVN() ? permutationCipher.encryptVN(input, key) : permutationCipher.encryptEN(input, key))
                : (isVN() ? permutationCipher.decryptVN(input, key) : permutationCipher.decryptEN(input, key));
    }

    // ────────────────────────────────────────────────────────────
    //  FILE – SAVE KEY
    // ────────────────────────────────────────────────────────────

    /**
     * Xây dựng nội dung file .key cho cipher đang active.
     * Hill dùng định dạng đặc biệt để lưu cả matrix lẫn originalLen.
     */
    private String buildKeyFileContent() {
        if (currentMethod.equals(METHOD_HILL)) {
            if (hillKeyMatrix == null) return null;
            StringBuilder sb = new StringBuilder();
            sb.append(HILL_MATRIX_PREFIX)
              .append(hillCipher.matrixToKey(hillKeyMatrix));
            if (hillOriginalLen >= 0) {
                sb.append("\n").append(HILL_ORIG_LEN_PREFIX).append(hillOriginalLen);
            }
            return sb.toString();
        }

        KeyPanel kp = view.getCurrentKeyPanel(currentMethod);
        return (kp != null) ? kp.getKeyText() : null;
    }

    // ────────────────────────────────────────────────────────────
    //  FILE – IMPORT KEY
    // ────────────────────────────────────────────────────────────

    /**
     * Phân tích chuỗi raw từ file và nạp vào panel / state tương ứng.
     */
    private void importKeyToCurrentPanel(String raw) {
        try {
            if (currentMethod.equals(METHOD_HILL)) {
                importHillKey(raw);
                return;
            }

            KeyPanel kp = view.getCurrentKeyPanel(currentMethod);
            if (kp == null) return;

            // Affine: có thể file lưu "7,3" — setKeyText() trong AffineConfigPanel tự parse
            kp.setKeyText(raw);

            // Xác nhận đã nạp
            JOptionPane.showMessageDialog(view.frame,
                    "Đã nạp khóa vào " + currentMethod + " thành công.",
                    "Import Key", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Không thể nạp khóa: " + ex.getMessage());
        }
    }

    /**
     * Parse và nạp file key dành riêng cho Hill.
     * Định dạng:
     *   matrix=<matrixToKey()>
     *   origLen=<n>         (tùy chọn)
     */
    private void importHillKey(String raw) throws Exception {
        String matrixStr = null;
        int    origLen   = -1;

        for (String line : raw.split("\\r?\\n")) {
            line = line.trim();
            if (line.startsWith(HILL_MATRIX_PREFIX)) {
                matrixStr = line.substring(HILL_MATRIX_PREFIX.length()).trim();
            } else if (line.startsWith(HILL_ORIG_LEN_PREFIX)) {
                origLen = Integer.parseInt(line.substring(HILL_ORIG_LEN_PREFIX.length()).trim());
            } else if (!line.isEmpty() && matrixStr == null) {
                // File chỉ chứa đúng một dòng matrix (không có prefix) — vẫn chấp nhận
                matrixStr = line;
            }
        }

        if (matrixStr == null || matrixStr.isBlank())
            throw new Exception("File không chứa dữ liệu ma trận hợp lệ!");

        // Parse matrix từ chuỗi nội bộ HillCipher
        hillKeyMatrix   = parseHillMatrix(matrixStr);
        hillOriginalLen = origLen;

        view.getHillPanel().setKeyDisplay(matrixStr);

        String msg = "Đã nạp khóa Hill thành công.";
        if (origLen >= 0) msg += "\n(origLen = " + origLen + ")";
        else              msg += "\n(origLen chưa được lưu — cần mã hóa trước khi giải mã)";

        JOptionPane.showMessageDialog(view.frame, msg, "Import Key", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Parse chuỗi "n;a00,a01,...,an-1n-1" trả về int[][].
     * Định dạng khớp với HillCipher.matrixToKey().
     *
     * Ví dụ matrixToKey({{3,3},{2,5}}) = "2;3,3,2,5"
     *   → n=2, values=[3,3,2,5] → {{3,3},{2,5}}
     */
    private int[][] parseHillMatrix(String key) throws Exception {
        // Lấy phần trước dấu ';' là kích thước n, phần sau là các số
        // (định dạng phụ thuộc implement của HillCipher.matrixToKey)
        // Nếu format khác: thử parse toàn bộ số ngăn cách bởi dấu phẩy/space
        try {
            String[] parts = key.split(";", 2);
            int n;
            int[] vals;

            if (parts.length == 2) {
                // Format "n;v0,v1,...,vn*n-1"
                n = Integer.parseInt(parts[0].trim());
                String[] tokens = parts[1].trim().split("[,\\s]+");
                vals = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++)
                    vals[i] = Integer.parseInt(tokens[i].trim());
            } else {
                // Fallback: thử format "v0,v1,...,vn*n-1" → cần biết n từ sqrt
                String[] tokens = key.trim().split("[,\\s]+");
                n = (int) Math.round(Math.sqrt(tokens.length));
                if (n * n != tokens.length)
                    throw new Exception("Không xác định được kích thước ma trận từ " + tokens.length + " phần tử!");
                vals = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++)
                    vals[i] = Integer.parseInt(tokens[i].trim());
            }

            if (vals.length != n * n)
                throw new Exception("Số phần tử ma trận không khớp: cần " + n*n + ", có " + vals.length + "!");

            int[][] matrix = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    matrix[i][j] = vals[i * n + j];
            return matrix;

        } catch (NumberFormatException ex) {
            throw new Exception("Dữ liệu ma trận chứa ký tự không hợp lệ!");
        }
    }

    // ────────────────────────────────────────────────────────────
    //  CLEAR
    // ────────────────────────────────────────────────────────────

    private void clearCurrentPanelKey() {
        switch (currentMethod) {
            case METHOD_CAESAR       -> view.getCaesarPanel().setKeyField("");
            case METHOD_SUBSTITUTION -> view.getSubstitutionPanel().getKeyArea().setText("");
            case METHOD_AFFINE       -> {
                view.getAffinePanel().getKeyA().setText("");
                view.getAffinePanel().getKeyB().setText("");
            }
            case METHOD_VIGENERE     -> view.getVigenerePanel().getKeyField().setText("");
            case METHOD_HILL         -> {
                hillKeyMatrix   = null;
                hillOriginalLen = -1;
                view.getHillPanel().setKeyDisplay("(Nhấn Gen Key để tạo khóa)");
            }
            case METHOD_PERMUTATION  -> view.getPermutationPanel().getKeyField().setText("");
        }
    }

    // ────────────────────────────────────────────────────────────
    //  VALIDATION
    // ────────────────────────────────────────────────────────────

    private void validateSubstitutionKey(String key) throws Exception {
        String alpha     = currentAlphabet();
        long alphaSize   = alpha.codePoints().count();
        long keySize     = key.codePoints().count();

        if (keySize != alphaSize)
            throw new Exception("Khóa phải có đúng " + alphaSize + " ký tự (hiện tại: " + keySize + ")!");

        Set<Integer> seen  = new HashSet<>();
        int[] alphaCps     = alpha.codePoints().toArray();
        for (int cp : key.codePoints().toArray()) {
            boolean inAlpha = false;
            for (int a : alphaCps) if (a == cp) { inAlpha = true; break; }
            if (!inAlpha)
                throw new Exception("Ký tự '" + new String(Character.toChars(cp)) + "' không thuộc bảng chữ cái!");
            if (!seen.add(cp))
                throw new Exception("Khóa chứa ký tự trùng lặp: '" + new String(Character.toChars(cp)) + "'!");
        }
    }

    private void validateAffineKey(int a, int b) throws Exception {
        int m = alphabetSize();
        if (a <= 0)           throw new Exception("Hệ số a phải là số nguyên dương!");
        if (b < 0)            throw new Exception("Hệ số b phải là số không âm!");
        if (!isCoprime(a, m)) throw new Exception(
                "Hệ số a=" + a + " không hợp lệ: gcd(a, " + m + ") ≠ 1 (không có nghịch đảo modulo)!");
        if (a == 1 && b == 0) throw new Exception("Bộ khóa (1, 0) không thay đổi văn bản — hãy chọn khóa khác!");
    }

    // ────────────────────────────────────────────────────────────
    //  UTILITIES
    // ────────────────────────────────────────────────────────────

    private boolean isVN()              { return LANG_VN.equals(currentLanguage); }
    private String  currentAlphabet()   { return isVN() ? Alphabet.VN_ALPHABET_FUL : Alphabet.EN_ALPHABET_FUL; }
    private int     alphabetSize()      { return (int) currentAlphabet().codePoints().count(); }

    private boolean isCoprime(int a, int m) {
        while (m != 0) { int t = m; m = a % m; a = t; }
        return a == 1;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(view.frame, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}