package controller.classic;

import controller.AppContext;


public class ClassicKeyValidator {

    private final AppContext ctx;

    public ClassicKeyValidator(AppContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Kiểm tra khóa Substitution: phải là hoán vị đầy đủ của bảng chữ cái hiện tại.
     */
    public void validateSubstitutionKey(String key) throws Exception {
        String alpha = ctx.currentAlphabet();
        long alphaSize = alpha.codePoints().count();

        if (key.codePoints().count() != alphaSize) {
            throw new Exception(
                "Khóa Substitution phải có đúng " + alphaSize + " ký tự (bằng kích thước bảng chữ cái)!"
            );
        }

        // Mỗi ký tự trong bảng chữ cái phải xuất hiện đúng một lần
        for (int cp : alpha.codePoints().toArray()) {
            String ch = new String(Character.toChars(cp));
            long count = key.codePoints()
                            .filter(c -> c == cp)
                            .count();
            if (count == 0) {
                throw new Exception("Khóa thiếu ký tự '" + ch + "' của bảng chữ cái!");
            }
            if (count > 1) {
                throw new Exception("Ký tự '" + ch + "' bị lặp lại trong khóa!");
            }
        }
    }

    /**
     * Kiểm tra khóa Affine: a phải nguyên tố cùng nhau với kích thước bảng chữ cái.
     */
    public void validateAffineKey(int a, int b) throws Exception {
        int size = ctx.alphabetSize();
        if (gcd(a, size) != 1) {
            throw new Exception(
                "Khóa 'a' = " + a + " phải nguyên tố cùng nhau với kích thước bảng chữ cái (" + size + ")!"
            );
        }
        if (a <= 0) {
            throw new Exception("Khóa 'a' phải là số nguyên dương!");
        }
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }
}
