package controller.symmetric;

import controller.AppContext;
import model.mordern.symmetric.SymmetricCipher;
import view.symmetric.SymmetricConfigPanel;

import javax.crypto.SecretKey;

public class SymmetricActionController {

    private final AppContext             ctx;
    private final SymmetricKeyValidator  keyValidator;

    public SymmetricActionController(AppContext ctx, SymmetricKeyValidator keyValidator) {
        this.ctx          = ctx;
        this.keyValidator = keyValidator;
    }

    public void bind() {
        bindGenKeys();
        bindEncryptDecrypt();
    }

    // ── Gen Key ──────────────────────────────────────────────────────────────

    private void bindGenKeys() {
        var sym = ctx.view.symmetricPanel;
        bindGenKey(sym.getAesPanel(),      ctx.aesModel,      true);
        bindGenKey(sym.getDesPanel(),      ctx.desModel,      false);
        bindGenKey(sym.getBlowfishPanel(), ctx.blowfishModel, true);
        bindGenKey(sym.getRc4Panel(),      ctx.rc4Model,      true);
    }

    private void bindGenKey(view.symmetric.SymmetricConfigPanel panel,
                            SymmetricCipher model,
                            boolean hasVariableKeySize) {
        panel.getGenBtn().addActionListener(e -> {
            String keyB64 = keyValidator.generateKeyBase64(panel, model, hasVariableKeySize);
            if (keyB64 != null) panel.setKeyText(keyB64);
        });
    }

    // ── Encrypt / Decrypt ────────────────────────────────────────────────────

    private void bindEncryptDecrypt() {
        ctx.view.sidePanel.getEncryptBtn().addActionListener(e -> {
            if (ctx.isSymmetricMode()) handleCrypto(true);
        });
        ctx.view.sidePanel.getDecryptBtn().addActionListener(e -> {
            if (ctx.isSymmetricMode()) handleCrypto(false);
        });
    }

    private void handleCrypto(boolean encrypt) {
        String input = ctx.view.ioPanel.getInputArea().getText().trim();
        if (!keyValidator.validateInput(input, encrypt)) return;

        SecretKey secretKey = keyValidator.resolveCurrentKey();
        if (secretKey == null) return;

        SymmetricCipher model = ctx.currentSymModel();
        SymmetricConfigPanel panel = ctx.view.symmetricPanel.getCurrentConfigPanel();

        try {
            // Áp dụng mode + padding trước khi thực hiện
            model.setTransformation(panel.getSelectedMode(), panel.getSelectedPadding());
            model.loadKey(secretKey);

            String result = encrypt ? model.encryptBase64(input)
                                    : model.decryptBase64(input);
            ctx.view.ioPanel.getOutputArea().setText(result);
            ctx.view.setStatus((encrypt ? "Mã hóa OK – " : "Giải mã OK – ")
                               + ctx.view.symmetricPanel.getCurrentAlgo()
                               + " [" + panel.getSelectedMode() + "/" + panel.getSelectedPadding() + "]");
        } catch (Exception ex) {
            ctx.showError((encrypt ? "Lỗi mã hóa: " : "Lỗi giải mã: ") + ex.getMessage());
        }
    }
}