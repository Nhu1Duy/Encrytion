package controller.hash;

import controller.AppContext;
import model.mordern.hash.HashFunction;
import util.FileManager;

import javax.swing.*;
import java.io.File;

/**
 * Xử lý sự kiện Hash File:
 *   - Mở file chooser chọn file nguồn
 *   - Tính hash của file bằng thuật toán đang chọn
 *   - Ghi kết quả hex ra outputArea
 *   - (Tuỳ chọn) Lưu kết quả hash ra file .txt
 */
public class HashFileController {

    private final AppContext ctx;
    private final HashActionController actionController;

    public HashFileController(AppContext ctx, HashActionController actionController) {
        this.ctx = ctx;
        this.actionController = actionController;
    }

    public void bind() {
        ctx.view.hashPanel.getConfigPanel()
                .getHashFileBtn()
                .addActionListener(e -> handleHashFile());

        // Gắn vào menu Save Output (dùng chung FileManager)
        ctx.view.itemSaveOutput.addActionListener(e -> {
            if (ctx.isHashMode()) saveOutput();
        });
    }

    // ── Hash File ─────────────────────────────────────────────────────────────

    private void handleHashFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file cần tính hash");
        fc.setAcceptAllFileFilterUsed(true);

        if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        String filePath = file.getAbsolutePath();

        try {
            HashFunction hashFn = actionController.resolveHashFunction();
            String result = hashFn.hashFile(filePath);

            ctx.view.ioPanel.getInputArea().setText("[FILE] " + filePath);
            ctx.view.ioPanel.getOutputArea().setText(result);
            ctx.view.setStatus("Hash File OK – " + ctx.view.hashPanel.getCurrentAlgo()
                    + " | " + file.getName());

        } catch (Exception ex) {
            ctx.showError("Lỗi hash file:\n" + ex.getMessage());
        }
    }

    // ── Save Output ───────────────────────────────────────────────────────────

    private void saveOutput() {
        String output = ctx.view.ioPanel.getOutputArea().getText().trim();
        if (output.isEmpty()) {
            ctx.showError("Chưa có kết quả hash để lưu.");
            return;
        }
        FileManager.saveText(ctx.view.frame, output);
    }
}