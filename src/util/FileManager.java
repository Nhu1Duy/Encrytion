package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    private static final String TXT_DESC = "Text files (*.txt)";
    private static final String KEY_DESC = "Key files (*.key, *.txt)";
    private static final String[] TXT_EXTS = new String[]{"txt"};
    private static final String[] KEY_EXTS = new String[]{"key", "txt"};

    public static String importText(Component parent) {
        File f = chooseOpenFile(parent, TXT_DESC, TXT_EXTS);
        return (f == null) ? null : readFile(parent, f);
    }

    public static boolean saveText(Component parent, String content) {
        File target = chooseSaveFile(parent, TXT_DESC, TXT_EXTS, "output.txt");
        if (target == null) return false;
        return writeFile(parent, target, content);
    }

    public static String importKey(Component parent) {
        File f = chooseOpenFile(parent, KEY_DESC, KEY_EXTS);
        if (f != null) {
            return readFile(parent, f);
        }
        return null;
    }

    public static boolean saveKey(Component parent, String content) {
        File target = chooseSaveFile(parent, KEY_DESC, KEY_EXTS, "secret.key");
        return target != null && writeFile(parent, target, content);
    }

    public static File chooseOpenFile(Component parent, String desc, String[] exts) {
        JFileChooser dialog = new JFileChooser();
        
        if (exts != null && exts.length > 0) {
            dialog.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(desc, exts);
            dialog.addChoosableFileFilter(filter);
        } else {
            dialog.setAcceptAllFileFilterUsed(true);
        }
        
        int state = dialog.showOpenDialog(parent);
        if (state != JFileChooser.APPROVE_OPTION) return null;
        
        return dialog.getSelectedFile();
    }

    public static File chooseDirectory(Component parent, File startDir) {
        JFileChooser browser = new JFileChooser();
        browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browser.setDialogTitle("Chọn thư mục lưu file");
        
        if (startDir != null && startDir.isDirectory()) {
            browser.setCurrentDirectory(startDir);
        }
        
        int op = browser.showOpenDialog(parent);
        return (op == JFileChooser.APPROVE_OPTION) ? browser.getSelectedFile() : null;
    }

    public static String generateOutputFileName(String src) {
        if (src == null || src.trim().length() == 0) {
            return "output.enc";
        }
        
        String name = new File(src).getName();
        int dotIdx = name.toLowerCase().lastIndexOf(".enc");
        if (dotIdx != -1 && dotIdx == name.length() - 4) {
            return name.substring(0, dotIdx);
        }
        return name + ".enc";
    }

    public static String buildOutputPath(String folder, String src) {
        StringBuilder path = new StringBuilder();
        path.append(folder).append(File.separator).append(generateOutputFileName(src));
        return path.toString();
    }

    private static File chooseSaveFile(Component parent, String desc, String[] exts, String def) {
        JFileChooser saver = new JFileChooser();
        saver.setAcceptAllFileFilterUsed(false);
        saver.setFileFilter(new FileNameExtensionFilter(desc, exts));
        saver.setSelectedFile(new File(def));
        
        if (saver.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File f = saver.getSelectedFile();
        String fileName = f.getName().toLowerCase();
        
        boolean validExt = false;
        for (int i = 0; i < exts.length; i++) {
            if (fileName.endsWith("." + exts[i].toLowerCase())) {
                validExt = true;
                break;
            }
        }
        
        if (!validExt) {
            f = new File(f.getParent(), f.getName() + "." + exts[0]);
        }

        if (f.exists()) {
            String msg = String.format("File '%s' đã tồn tại. Bạn có muốn ghi đè?", f.getName());
            int confirm = JOptionPane.showConfirmDialog(parent, msg, "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return null;
        }
        
        return f;
    }

    private static String readFile(Component parent, File f) {
        try {
            byte[] encoded = Files.readAllBytes(f.toPath());
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            showMsg(parent, "Lỗi hệ thống", "Không thể đọc dữ liệu: " + ex.getLocalizedMessage());
            return null;
        }
    }

    private static boolean writeFile(Component parent, File f, String content) {
        try {
            Path targetPath = f.toPath();
            Files.write(targetPath, content.getBytes(StandardCharsets.UTF_8));
            showMsg(parent, "Thông báo", "Lưu file thành công!", true);
            return true;
        } catch (IOException error) {
            showMsg(parent, "Thất bại", "Lỗi khi ghi file: " + error.getMessage(), false);
            return false;
        }
    }

    private static void showMsg(Component p, String title, String msg) {
        showMsg(p, title, msg, false);
    }

    private static void showMsg(Component p, String title, String msg, boolean isOk) {
        int type = isOk ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(p, msg, title, type);
    }
}