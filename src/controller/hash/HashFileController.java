package controller.hash;

import controller.AppContext;
import model.mordern.hash.HashFunction;
import util.FileManager;

import javax.swing.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HashFileController implements ActionListener {

    private final AppContext ctx;
    private final HashActionController action;

    public HashFileController(AppContext ctx, HashActionController action) {
        this.ctx = ctx;
        this.action = action;
    }

    public void bind() {
        ctx.view.hashPanel.getConfigPanel().getHashFileBtn().addActionListener(this);
        
        ctx.view.itemSaveOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ctx.isHashMode()) {
                    executeSaveResult();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        performHashProcess();
    }

    private void performHashProcess() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Hệ thống: Chọn file cần tính mã băm");
        
        int resultState = chooser.showOpenDialog(ctx.view.frame);
        if (resultState != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = chooser.getSelectedFile();
        if (targetFile == null || !targetFile.exists()) {
            return;
        }

        try {
            HashFunction hashFunc = action.resolveHashFunction();
            String absolutePath = targetFile.getAbsolutePath();
            
            String hashValue = hashFunc.hashFile(absolutePath);

            ctx.view.ioPanel.getInputArea().setText("[PATH]: " + absolutePath);
            ctx.view.ioPanel.getOutputArea().setText(hashValue);

            String algorithmName = ctx.view.hashPanel.getCurrentAlgo();
            String statusMsg = String.format("Hash OK - %s | %s", algorithmName, targetFile.getName());
            
            ctx.view.setStatus(statusMsg);
            JOptionPane.showMessageDialog(ctx.view.frame, "Tính toán mã băm hoàn tất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            ex.printStackTrace(); 
            ctx.showError("Phát sinh lỗi trong quá trình hash file:\n" + ex.getMessage());
        }
    }

    private void executeSaveResult() {
        String outputText = ctx.view.ioPanel.getOutputArea().getText();
        
        if (outputText == null || outputText.trim().isEmpty()) {
            ctx.showError("Không tìm thấy dữ liệu băm để thực hiện lưu trữ.");
            return;
        }

        try {
            FileManager.saveText(ctx.view.frame, outputText.trim());
            System.out.println("save clicked");
        } catch (Exception ex) {
            ctx.showError("Lỗi khi ghi tệp: " + ex.getMessage());
        }
    }
}