package controller;

import javax.swing.JOptionPane;

/**
 * ActionController – xử lý các hành động Encrypt, Decrypt và Gen Key
 * cho từng thuật toán mã hóa.
 */
public class ActionController {

    private final ControllerContext ctx;
    private final KeyValidator      validator;

    public ActionController(ControllerContext ctx, KeyValidator validator) {
        this.ctx       = ctx;
        this.validator = validator;
    }

    public void bind() {
        bindEncryptDecrypt();
        bindGenKeys();
    }

    // ── Encrypt / Decrypt ────────────────────────────────────────

    private void bindEncryptDecrypt() {
        ctx.view.getEncryptBtn().addActionListener(e -> handleAction(true));
        ctx.view.getDecryptBtn().addActionListener(e -> handleAction(false));
    }

    private void handleAction(boolean isEncrypt) {
        String input = ctx.view.getInputArea().getText().trim();
        if (input.isEmpty()) {
            ctx.showError("Vui lòng nhập văn bản!");
            return;
        }
        try {
            String result = switch (ctx.currentMethod) {
                case ControllerContext.METHOD_CAESAR       -> handleCaesar(input, isEncrypt);
                case ControllerContext.METHOD_SUBSTITUTION -> handleSubstitution(input, isEncrypt);
                case ControllerContext.METHOD_AFFINE        -> handleAffine(input, isEncrypt);
                case ControllerContext.METHOD_VIGENERE      -> handleVigenere(input, isEncrypt);
                case ControllerContext.METHOD_HILL          -> handleHill(input, isEncrypt);
                case ControllerContext.METHOD_PERMUTATION   -> handlePermutation(input, isEncrypt);
                default -> throw new IllegalStateException("Phương pháp không hợp lệ: " + ctx.currentMethod);
            };
            ctx.view.getOutputArea().setText(result);
        } catch (NumberFormatException ex) {
            ctx.showError("Khóa phải là số hợp lệ!");
        } catch (Exception ex) {
            ctx.showError(ex.getMessage());
        }
    }

    // ── Cipher handlers ──────────────────────────────────────────

    private String handleCaesar(String input, boolean isEncrypt) throws Exception {
        String keyStr = ctx.view.getCaesarPanel().getTextKeyField().trim();
        if (keyStr.isEmpty()) throw new Exception("Vui lòng nhập khóa hoặc nhấn 'Gen Key' để tạo!");
        return isEncrypt
                ? (ctx.isVN() ? ctx.caesarCipher.encryptVN(input, keyStr) : ctx.caesarCipher.encryptEN(input, keyStr))
                : (ctx.isVN() ? ctx.caesarCipher.decryptVN(input, keyStr) : ctx.caesarCipher.decryptEN(input, keyStr));
    }

    private String handleSubstitution(String input, boolean isEncrypt) throws Exception {
        String key = ctx.view.getSubstitutionPanel().getKeyArea().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa không được để trống!");
        validator.validateSubstitutionKey(key);
        return isEncrypt
                ? (ctx.isVN() ? ctx.substitutionCipher.encryptVN(input, key) : ctx.substitutionCipher.encryptEN(input, key))
                : (ctx.isVN() ? ctx.substitutionCipher.decryptVN(input, key) : ctx.substitutionCipher.decryptEN(input, key));
    }

    private String handleAffine(String input, boolean isEncrypt) throws Exception {
        String aStr = ctx.view.getAffinePanel().getTextKeyA().trim();
        String bStr = ctx.view.getAffinePanel().getTextKeyB().trim();
        if (aStr.isEmpty() || bStr.isEmpty()) throw new Exception("Cả a và b không được để trống!");
        int a = Integer.parseInt(aStr);
        int b = Integer.parseInt(bStr);
        validator.validateAffineKey(a, b);
        String key = a + "," + b;
        return isEncrypt
                ? (ctx.isVN() ? ctx.affineCipher.encryptVN(input, key) : ctx.affineCipher.encryptEN(input, key))
                : (ctx.isVN() ? ctx.affineCipher.decryptVN(input, key) : ctx.affineCipher.decryptEN(input, key));
    }

    private String handleVigenere(String input, boolean isEncrypt) throws Exception {
        String key = ctx.view.getVigenerePanel().getKeyField().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa không được để trống!");
        return isEncrypt
                ? (ctx.isVN() ? ctx.vigenereCipher.encryptVN(input, key) : ctx.vigenereCipher.encryptEN(input, key))
                : (ctx.isVN() ? ctx.vigenereCipher.decryptVN(input, key) : ctx.vigenereCipher.decryptEN(input, key));
    }

    private String handleHill(String input, boolean isEncrypt) throws Exception {
        if (ctx.hillKeyMatrix == null)
            throw new Exception("Vui lòng nhấn 'Gen Key' để tạo khóa Hill trước!");
        if (isEncrypt) {
            String alpha = ctx.currentAlphabet();
            ctx.hillOriginalLen = (int) input.codePoints()
                    .filter(cp -> alpha.contains(new String(Character.toChars(cp))))
                    .count();
            return ctx.isVN()
                    ? ctx.hillCipher.encryptVN(input, ctx.hillKeyMatrix)
                    : ctx.hillCipher.encryptEN(input, ctx.hillKeyMatrix);
        } else {
            if (ctx.hillOriginalLen < 0)
                throw new Exception("Hãy mã hóa văn bản trước rồi mới giải mã (cần biết độ dài gốc)!");
            return ctx.isVN()
                    ? ctx.hillCipher.decryptVN(input, ctx.hillKeyMatrix, ctx.hillOriginalLen)
                    : ctx.hillCipher.decryptEN(input, ctx.hillKeyMatrix, ctx.hillOriginalLen);
        }
    }

    private String handlePermutation(String input, boolean isEncrypt) throws Exception {
        String key = ctx.view.getPermutationPanel().getKeyField().getText().trim();
        if (key.isEmpty()) throw new Exception("Khóa hoán vị không được để trống!");
        return isEncrypt
                ? (ctx.isVN() ? ctx.permutationCipher.encryptVN(input, key) : ctx.permutationCipher.encryptEN(input, key))
                : (ctx.isVN() ? ctx.permutationCipher.decryptVN(input, key) : ctx.permutationCipher.decryptEN(input, key));
    }

    // ── Gen Key ──────────────────────────────────────────────────

    private void bindGenKeys() {
        bindCaesarGenKey();
        bindSubstitutionGenKey();
        bindAffineGenKey();
        bindVigenereGenKey();
        bindHillGenKey();
        bindPermutationGenKey();
    }

    private void bindCaesarGenKey() {
        ctx.view.getCaesarPanel().getGenBtn().addActionListener(e -> {
            try {
                String maxStr = ctx.view.getCaesarPanel().getTextKeyLenField().trim();
                int max = maxStr.isEmpty() ? ctx.alphabetSize() : Integer.parseInt(maxStr);
                ctx.view.getCaesarPanel().setKeyField(String.valueOf(ctx.caesarCipher.genKey(max)));
            } catch (NumberFormatException ex) {
                ctx.showError("Giới hạn phải là số nguyên dương!");
            }
        });
    }

    private void bindSubstitutionGenKey() {
        ctx.view.getSubstitutionPanel().getGenBtn().addActionListener(e ->
                ctx.view.getSubstitutionPanel().getKeyArea().setText(
                        ctx.substitutionCipher.genKey(ctx.currentAlphabet())));
    }

    private void bindAffineGenKey() {
        ctx.view.getAffinePanel().getGenBtn().addActionListener(e -> {
            int[] keys = ctx.affineCipher.genKey(ctx.alphabetSize());
            ctx.view.getAffinePanel().getKeyA().setText(String.valueOf(keys[0]));
            ctx.view.getAffinePanel().getKeyB().setText(String.valueOf(keys[1]));
        });
    }

    private void bindVigenereGenKey() {
        ctx.view.getVigenerePanel().getGenBtn().addActionListener(e -> {
            try {
                String lenStr = ctx.view.getVigenerePanel().getTextKeyLenField().trim();
                int length = lenStr.isEmpty() ? 8 : Integer.parseInt(lenStr);
                ctx.view.getVigenerePanel().getKeyField().setText(
                        ctx.vigenereCipher.genKey(ctx.currentAlphabet(), length));
            } catch (NumberFormatException ex) {
                ctx.showError("Độ dài khóa phải là số nguyên dương!");
            }
        });
    }

    private void bindHillGenKey() {
        ctx.view.getHillPanel().getGenBtn().addActionListener(e -> {
            try {
                String sizeStr = ctx.view.getHillPanel().getSizeField().trim();
                int n = sizeStr.isEmpty() ? 2 : Integer.parseInt(sizeStr);
                if (n < 2 || n > 5) throw new IllegalArgumentException("Kích thước ma trận phải từ 2 đến 5!");

                ctx.hillKeyMatrix   = ctx.isVN() ? ctx.hillCipher.generateKeyVN(n) : ctx.hillCipher.generateKeyEN(n);
                ctx.hillOriginalLen = -1;

                ctx.view.getHillPanel().setKeyDisplay(ctx.hillCipher.matrixToKey(ctx.hillKeyMatrix));
            } catch (NumberFormatException ex) {
                ctx.showError("Kích thước ma trận phải là số nguyên!");
            } catch (IllegalArgumentException ex) {
                ctx.showError(ex.getMessage());
            }
        });
    }

    private void bindPermutationGenKey() {
        ctx.view.getPermutationPanel().getGenBtn().addActionListener(e -> {
            try {
                String lenStr = ctx.view.getPermutationPanel().getLenField().getText().trim();
                int length = lenStr.isEmpty() ? 5 : Integer.parseInt(lenStr);
                ctx.view.getPermutationPanel().getKeyField().setText(
                        ctx.permutationCipher.genKey(length));
            } catch (NumberFormatException ex) {
                ctx.showError("Độ dài khóa hoán vị phải là số nguyên dương!");
            }
        });
    }
}
