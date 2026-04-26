package controller;

import model.mordern.symmetric.AES;
import model.mordern.symmetric.Blowfish;
import model.mordern.symmetric.DES;
import model.mordern.symmetric.RC4;
import model.mordern.symmetric.SymmetricCipher;
import view.MainFrame;
import view.SymmetricConfigPanel;
import view.SymmetricPanel;

import javax.swing.*;

/**
 * Controller cho nhóm thuật toán Symmetric (AES, DES, Blowfish, RC4).
 *
 * Trách nhiệm:
 *   - bind nút Gen Key của từng SymmetricConfigPanel
 *   - bind nút Encrypt / Decrypt của MainFrame (khi đang ở mode Symmetric)
 *   - ủy quyền thực thi sang model tương ứng
 *
 * Được khởi tạo và bind trong CryptoController.
 */
public class SymmetricController {

    private final ControllerContext ctx;

    // Models — mỗi instance giữ state key riêng
    private final AES      aesModel      = new AES();
    private final DES      desModel      = new DES();
    private final Blowfish blowfishModel = new Blowfish();
    private final RC4      rc4Model      = new RC4();

    public SymmetricController(ControllerContext ctx) {
        this.ctx = ctx;
    }

    // ------------------------------------------------------------------ //
    //  Bind
    // ------------------------------------------------------------------ //

    /**
     * Gọi một lần từ CryptoController sau khi MainFrame đã sẵn sàng.
     */
    public void bind() {
        bindGenKeys();
        bindEncryptDecrypt();
    }

    // ------------------------------------------------------------------ //
    //  Gen Key buttons
    // ------------------------------------------------------------------ //

    private void bindGenKeys() {
        MainFrame    view = ctx.view;
        SymmetricPanel sp = view.getSymmetricPanel();

        bindGenKey(sp.getAesPanel(),      aesModel);
        bindGenKey(sp.getDesPanel(),      desModel);
        bindGenKey(sp.getBlowfishPanel(), blowfishModel);
        bindGenKey(sp.getRc4Panel(),      rc4Model);
    }

    private void bindGenKey(SymmetricConfigPanel panel, SymmetricCipher model) {
        panel.getGenBtn().addActionListener(e -> {
            try {
                int keySize = panel.getSelectedKeySize();
                model.genKey(keySize);
                panel.setKeyText(model.getKeyAsBase64());
            } catch (Exception ex) {
                showError("Lỗi sinh key: " + ex.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------ //
    //  Encrypt / Decrypt buttons (MainFrame chung)
    // ------------------------------------------------------------------ //

    private void bindEncryptDecrypt() {
        MainFrame view = ctx.view;

        view.getEncryptBtn().addActionListener(e -> {
            if (!isSymmetricMode()) return;
            handleCrypto(true);
        });

        view.getDecryptBtn().addActionListener(e -> {
            if (!isSymmetricMode()) return;
            handleCrypto(false);
        });
    }

    private void handleCrypto(boolean encrypt) {
        MainFrame      view = ctx.view;
        SymmetricPanel sp   = view.getSymmetricPanel();

        String inputText = view.getInputArea().getText().trim();
        if (inputText.isEmpty()) {
            showError("Vui lòng nhập văn bản cần " + (encrypt ? "mã hóa" : "giải mã") + "!");
            return;
        }

        SymmetricConfigPanel configPanel = sp.getCurrentConfigPanel();
        String keyB64 = configPanel.getKeyText();
        if (keyB64.isEmpty()) {
            showError("Vui lòng nhập key hoặc nhấn 'Gen Key' để tạo!");
            return;
        }

        SymmetricCipher model = getModel(sp.getCurrentAlgo());
        try {
            model.loadKeyFromBase64(keyB64);
            String result = encrypt
                    ? model.encryptText(inputText)
                    : model.decryptText(inputText);
            view.getOutputArea().setText(result);
            // setLanguageStatus tái dụng statusLabel để thông báo kết quả
            view.setLanguageStatus(
                    (encrypt ? "Mã hóa" : "Giải mã") + " OK – " + sp.getCurrentAlgo());
        } catch (Exception ex) {
            showError((encrypt ? "Lỗi mã hóa: " : "Lỗi giải mã: ") + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

    /** Chỉ xử lý khi MainFrame đang hiển thị mode Symmetric. */
    private boolean isSymmetricMode() {
        return ControllerContext.METHOD_SYMMETRIC.equals(ctx.currentMethod);
    }

    private SymmetricCipher getModel(String algo) {
        return switch (algo) {
            case SymmetricPanel.ALGO_AES      -> aesModel;
            case SymmetricPanel.ALGO_DES      -> desModel;
            case SymmetricPanel.ALGO_BLOWFISH -> blowfishModel;
            case SymmetricPanel.ALGO_RC4      -> rc4Model;
            default -> aesModel;
        };
    }

    /**
     * Dùng null làm parent — JOptionPane tự căn giữa màn hình.
     * Không ép kiểu MainFrame thành Component (MainFrame không extends JFrame).
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}