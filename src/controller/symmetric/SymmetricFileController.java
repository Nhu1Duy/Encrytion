package controller.symmetric;

import controller.AppContext;
import util.FileManager;
import view.symmetric.SymmetricConfigPanel;

import javax.crypto.SecretKey;

public class SymmetricFileController {

    private final AppContext            ctx;
    private final SymmetricKeyValidator keyValidator;

    public SymmetricFileController(AppContext ctx, SymmetricKeyValidator keyValidator) {
        this.ctx          = ctx;
        this.keyValidator = keyValidator;
    }

    public void bind() {
        ctx.view.itemImportKey.addActionListener(e -> importKey());
        ctx.view.itemSaveKey.addActionListener(e -> saveKey());
    }

    // ── Import Key ───────────────────────────────────────────────────────────

    private void importKey() {
        String keyText = FileManager.importKey(ctx.view.frame);
        if (keyText == null || keyText.isBlank()) return;
        ctx.view.symmetricPanel.getCurrentConfigPanel().setKeyText(keyText.trim());
    }

    // ── Save Key ─────────────────────────────────────────────────────────────

    private void saveKey() {
        String keyText = ctx.view.symmetricPanel.getCurrentConfigPanel().getKeyText();
        if (keyText.isBlank()) {
            ctx.showError("Chưa có key để lưu. Vui lòng tạo hoặc nhập key trước.");
            return;
        }
        FileManager.saveKey(ctx.view.frame, keyText);
    }

    // ── Process File (optional) ───────────────────────────────────────────────

    public boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception {
        SecretKey secretKey = keyValidator.resolveCurrentKey();
        if (secretKey == null) return false;
        ctx.currentSymModel().loadKey(secretKey);
        return ctx.currentSymModel().processFile(sourceFile, destFile, encrypt);
    }
}