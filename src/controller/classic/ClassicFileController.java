package controller.classic;

import controller.AppContext;
import util.FileManager;
import view.classic.ClassicCipherPanel;
import view.shared.KeyPanel;

import javax.swing.*;

/**
 * ClassicFileController — Import/Export Key và text cho nhóm thuật toán cổ điển.
 */
public class ClassicFileController {

    private final AppContext         ctx;
    private final ClassicCipherPanel classicView;
    private final HillKeyParser      hillKeyParser;

    public ClassicFileController(AppContext ctx, HillKeyParser hillKeyParser) {
        this.ctx           = ctx;
        this.classicView   = ctx.view.classicPanel;
        this.hillKeyParser = hillKeyParser;
    }

    public void bind() {
        bindImportInput();
        bindSaveOutput();
        bindImportKey();
        bindSaveKey();
        bindClearAll();
    }

    private void bindImportInput() {
        ctx.view.itemImportInput.addActionListener(e -> {
            String content = FileManager.importText(ctx.view.frame);
            if (content != null) ctx.view.ioPanel.getInputArea().setText(content);
        });
    }

    private void bindSaveOutput() {
        ctx.view.itemSaveOutput.addActionListener(e -> {
            String content = ctx.view.ioPanel.getOutputArea().getText();
            if (content.isBlank()) { ctx.showError("Chua co ket qua de luu!"); return; }
            FileManager.saveText(ctx.view.frame, content);
        });
    }

    private void bindImportKey() {
        ctx.view.itemImportKey.addActionListener(e -> {
            if (!ctx.isClassicMode()) return;
            String raw = FileManager.importKey(ctx.view.frame);
            if (raw == null) return;
            importKeyToPanel(raw.trim());
        });
    }

    private void importKeyToPanel(String raw) {
        try {
            if (ClassicCipherPanel.HILL.equals(ctx.classicMethod)) {
                hillKeyParser.importHillKey(raw);
                return;
            }
            KeyPanel kp = classicView.getKeyPanel(ctx.classicMethod);
            if (kp == null) return;
            kp.setKeyText(raw);
            ctx.showInfo("Da nap khoa vao " + ctx.classicMethod + " thanh cong.");
        } catch (Exception ex) {
            ctx.showError("Khong the nap khoa: " + ex.getMessage());
        }
    }

    private void bindSaveKey() {
        ctx.view.itemSaveKey.addActionListener(e -> {
            if (!ctx.isClassicMode()) return;
            String content = buildKeyContent();
            if (content == null || content.isBlank()) {
                ctx.showError("Chua co khoa de luu!");
                return;
            }
            FileManager.saveKey(ctx.view.frame, content);
        });
    }

    private String buildKeyContent() {
        if (ClassicCipherPanel.HILL.equals(ctx.classicMethod)) {
            if (ctx.hillKeyMatrix == null) return null;
            StringBuilder sb = new StringBuilder();
            sb.append(HillKeyParser.HILL_MATRIX_PREFIX)
              .append(ctx.hillCipher.matrixToKey(ctx.hillKeyMatrix));
            if (ctx.hillOriginalLen >= 0)
                sb.append("\n").append(HillKeyParser.HILL_ORIG_LEN_PREFIX).append(ctx.hillOriginalLen);
            return sb.toString();
        }
        KeyPanel kp = classicView.getKeyPanel(ctx.classicMethod);
        return kp != null ? kp.getKeyText() : null;
    }

    private void bindClearAll() {
        ctx.view.itemClearAll.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                ctx.view.frame, "Xoa toan bo Input, Output va Key hien tai?",
                "Xac nhan xoa", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            ctx.view.ioPanel.getInputArea().setText("");
            ctx.view.ioPanel.getOutputArea().setText("");
            clearCurrentKey();
        });
    }

    private void clearCurrentKey() {
        switch (ctx.classicMethod) {
            case ClassicCipherPanel.CAESAR      -> classicView.getCaesarPanel().setKeyField("");
            case ClassicCipherPanel.SUBSTITUTION -> classicView.getSubstitutionPanel().getKeyArea().setText("");
            case ClassicCipherPanel.AFFINE -> {
                classicView.getAffinePanel().getKeyA().setText("");
                classicView.getAffinePanel().getKeyB().setText("");
            }
            case ClassicCipherPanel.VIGENERE    -> classicView.getVigenerePanel().getKeyField().setText("");
            case ClassicCipherPanel.HILL -> {
                ctx.hillKeyMatrix   = null;
                ctx.hillOriginalLen = -1;
                classicView.getHillPanel().setKeyDisplay("(Nhan Gen Key de tao khoa)");
            }
            case ClassicCipherPanel.PERMUTATION -> classicView.getPermutationPanel().getKeyField().setText("");
        }
    }
}
