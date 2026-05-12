package controller.symmetric;

import controller.AppContext;
import model.mordern.symmetric.*;
import view.symmetric.SymmetricConfigPanel;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SymmetricKeyValidator {

    private final AppContext ctx;

    public SymmetricKeyValidator(AppContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Lấy key từ panel hiện tại, decode Base64 và wrap thành SecretKey.
     * Trả về null nếu rỗng hoặc không hợp lệ.
     */
    public SecretKey resolveCurrentKey() {
        String keyB64 = ctx.view.symmetricPanel.getCurrentConfigPanel().getKeyText();
        if (keyB64.isBlank()) {
            ctx.showError("Vui lòng nhập key hoặc nhấn '⚡ Gen Key' để tạo!");
            return null;
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyB64);
            return new SecretKeySpec(keyBytes, ctx.currentSymAlgoName());
        } catch (IllegalArgumentException ex) {
            ctx.showError("Key không hợp lệ (không phải Base64):\n" + ex.getMessage());
            return null;
        }
    }

    /**
     * Validate input text không rỗng.
     */
    public boolean validateInput(String text, boolean encrypt) {
        if (text == null || text.isBlank()) {
            ctx.showError(encrypt ? "Vui lòng nhập văn bản cần mã hóa!"
                                  : "Vui lòng nhập văn bản cần giải mã!");
            return false;
        }
        return true;
    }

    /**
     * Gen key cho một panel + model cụ thể, trả về Base64.
     * hasVariableKeySize = false với DES (key size cố định).
     */
    public String generateKeyBase64(SymmetricConfigPanel panel,
                                    SymmetricCipher model,
                                    boolean hasVariableKeySize) {
        try {
            if (hasVariableKeySize) applyKeySize(model, panel.getSelectedKeySize());
            SecretKey key = model.genKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception ex) {
            ctx.showError("Lỗi sinh key: " + ex.getMessage());
            return null;
        }
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private void applyKeySize(SymmetricCipher model, int keySize) {
        if      (model instanceof AES m)      m.setKeySize(keySize);
        else if (model instanceof Blowfish m) m.setKeySize(keySize);
        else if (model instanceof RC4 m)      m.setKeySize(keySize);
        // DES: cố định – bỏ qua
    }
}