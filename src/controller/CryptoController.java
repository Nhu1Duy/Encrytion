package controller;

import model.clasic.AffineCipher;
import model.clasic.CaesarCipher;
import model.clasic.HillCipher;
import model.clasic.PermutationCipher;
import model.clasic.SubstitutionCipher;
import model.clasic.VigenereCipher;
import view.MainFrame;
import Tool.Alphabet;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * CryptoController – kết nối View và Model theo mô hình MVC.
 *
 * Quy ước method ID (dùng trong CardLayout và switch-case):
 *   "Caesar"       – Dịch Chuyển
 *   "Substitution" – Thay Thế
 *   "Affine"       – Affine
 *   "Vigenere"     – Vigenere
 *   "Hill"         – Hill
 *   "Permutation"  – Hoán Vị
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
    private int[][] hillKeyMatrix    = null;
    private int     hillOriginalLen  = -1;

    // ── Constructor ──────────────────────────────────────────────
    public CryptoController(MainFrame view) {
        this.view = view;
        bindMenuListeners();
        bindActionListeners();
        bindGenKeyListeners();
    }

    // ── Bind menu ────────────────────────────────────────────────
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

    // ── Bind encrypt/decrypt buttons ─────────────────────────────
    private void bindActionListeners() {
        view.getEncryptBtn().addActionListener(e -> handleAction(true));
        view.getDecryptBtn().addActionListener(e -> handleAction(false));
    }

    // ── Bind Gen Key buttons ─────────────────────────────────────
    private void bindGenKeyListeners() {
        // Caesar
        view.getCaesarPanel().getGenBtn().addActionListener(e -> {
            try {
                String maxStr = view.getCaesarPanel().getTextKeyLenField().trim();
                int max = maxStr.isEmpty() ? alphabetSize() : Integer.parseInt(maxStr);
                int key = caesarCipher.genKey(max);
                view.getCaesarPanel().setKeyField(String.valueOf(key));
            } catch (NumberFormatException ex) {
                showError("Giới hạn phải là số nguyên dương!");
            }
        });

        // Substitution
        view.getSubstitutionPanel().getGenBtn().addActionListener(e -> {
            String key = substitutionCipher.genKey(currentAlphabet());
            view.getSubstitutionPanel().getKeyArea().setText(key);
        });

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
                String key = vigenereCipher.genKey(currentAlphabet(), length);
                view.getVigenerePanel().getKeyField().setText(key);
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

                hillKeyMatrix = isVN()
                        ? hillCipher.generateKeyVN(n)
                        : hillCipher.generateKeyEN(n);
                hillOriginalLen = -1; // reset khi gen key mới

                String display = hillCipher.matrixToKey(hillKeyMatrix);
                view.getHillPanel().setKeyDisplay(display);
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
                String key = permutationCipher.genKey(length);
                view.getPermutationPanel().getKeyField().setText(key);
            } catch (NumberFormatException ex) {
                showError("Độ dài khóa hoán vị phải là số nguyên dương!");
            }
        });
    }

    // ── Switch method ────────────────────────────────────────────
    private void switchMethod(String methodId) {
        currentMethod = methodId;
        view.showLayout(methodId);
    }

    // ── Main encrypt/decrypt handler ─────────────────────────────
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
        // Delegate parsing to model (parseKey inside CaesarCipher accepts String)
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

        // Model nhận key dạng "a,b"
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
            // Đếm ký tự thuộc alphabet để lưu originalLength cho decrypt
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

    // ── Validation helpers ────────────────────────────────────────

    private void validateSubstitutionKey(String key) throws Exception {
        String alpha = currentAlphabet();
        long alphaSize = alpha.codePoints().count();
        long keySize   = key.codePoints().count();

        if (keySize != alphaSize)
            throw new Exception("Khóa phải có đúng " + alphaSize + " ký tự (hiện tại: " + keySize + ")!");

        // Kiểm tra mỗi ký tự thuộc alphabet và không trùng
        Set<Integer> seen = new HashSet<>();
        int[] alphaCps = alpha.codePoints().toArray();
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
        if (a <= 0) throw new Exception("Hệ số a phải là số nguyên dương!");
        if (b < 0)  throw new Exception("Hệ số b phải là số không âm!");
        if (!isCoprime(a, m))
            throw new Exception("Hệ số a=" + a + " không hợp lệ: gcd(a, " + m + ") ≠ 1 (không có nghịch đảo modulo)!");
        if (a == 1 && b == 0)
            throw new Exception("Bộ khóa (1, 0) không thay đổi văn bản — hãy chọn khóa khác!");
    }

    // ── Utilities ─────────────────────────────────────────────────

    private boolean isVN() { return LANG_VN.equals(currentLanguage); }

    private String currentAlphabet() {
        return isVN() ? Alphabet.VN_ALPHABET_FUL : Alphabet.EN_ALPHABET_FUL;
    }

    private int alphabetSize() {
        return (int) currentAlphabet().codePoints().count();
    }

    private boolean isCoprime(int a, int m) {
        while (m != 0) { int t = m; m = a % m; a = t; }
        return a == 1;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}