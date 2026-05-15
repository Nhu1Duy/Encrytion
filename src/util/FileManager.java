package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileManager {

    private static final String TXT_DESC = "Text files (*.txt)";
    private static final String KEY_DESC = "Key files (*.key, *.txt)";
    private static final String[] TXT_EXT = {"txt"};
    private static final String[] KEY_EXT = {"key", "txt"};

    public static String importText(Component parent) {
        File f = chooseOpenFile(parent, TXT_DESC, TXT_EXT);
        if (f == null) return null;
        return readFile(parent, f);
    }

    public static boolean saveText(Component parent, String content) {
        File f = chooseSaveFile(parent, TXT_DESC, TXT_EXT, "output.txt");
        if (f == null) return false;
        return writeFile(parent, f, content);
    }

    public static String importKey(Component parent) {
        File f = chooseOpenFile(parent, KEY_DESC, KEY_EXT);
        if (f == null) return null;
        return readFile(parent, f);
    }

    public static boolean saveKey(Component parent, String content) {
        File f = chooseSaveFile(parent, KEY_DESC, KEY_EXT, "secret.key");
        if (f == null) return false;
        return writeFile(parent, f, content);
    }

    public static File chooseOpenFile(Component parent, String desc, String[] exts) {
        JFileChooser chooser = new JFileChooser();
        
        if (exts != null && exts.length > 0) {
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter(desc, exts));
        } else {
            chooser.setAcceptAllFileFilterUsed(true);
        }
        
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static File chooseDirectory(Component parent, File startDir) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Chọn thư mục lưu file output");
        
        if (startDir != null && startDir.exists()) {
            chooser.setCurrentDirectory(startDir);
        }
        
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static String generateOutputFileName(String src) {
        if (src == null || src.isEmpty()) {
            return "output.enc";
        }
        
        String nm = new File(src).getName();
        int p = nm.lastIndexOf(".enc");
        
        if (p > 0 && p == nm.length() - 4) {
            return nm.substring(0, p);
        }
        return nm + ".enc";
    }

    public static String buildOutputPath(String folder, String src) {
        return folder + File.separator + generateOutputFileName(src);
    }

    private static File chooseSaveFile(Component parent, String desc, String[] exts, String def) {
        JFileChooser dlg = new JFileChooser();
        dlg.setAcceptAllFileFilterUsed(false);
        dlg.addChoosableFileFilter(new FileNameExtensionFilter(desc, exts));
        dlg.setSelectedFile(new File(def));
        
        int res = dlg.showSaveDialog(parent);
        if (res != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File f = dlg.getSelectedFile();
        String nm = f.getName();
        boolean ext_ok = false;
        
        for (String e : exts) {
            if (nm.toLowerCase().contains("." + e)) {
                ext_ok = true;
                break;
            }
        }
        
        if (!ext_ok) {
            f = new File(f.getParentFile(), nm + "." + exts[0]);
        }

        if (f.exists()) {
            String prompt = "File " + f.getName() + " tồn tại\nThay thế?";
            int opt = JOptionPane.showConfirmDialog(parent, prompt, "Ghi đè",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        
        return f;
    }

    private static String readFile(Component parent, File f) {
        try {
            return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            showMsg(parent, "Lỗi", "Đọc file không được: " + ex.getMessage());
            return null;
        }
    }

    private static boolean writeFile(Component parent, File f, String txt) {
        try {
            Files.write(f.toPath(), txt.getBytes(StandardCharsets.UTF_8));
            showMsg(parent, "OK", "Đã lưu: " + f.getAbsolutePath());
            return true;
        } catch (IOException ex) {
            showMsg(parent, "Lỗi", "Lưu file không được: " + ex.getMessage());
            return false;
        }
    }

    private static void showMsg(Component p, String t, String m) {
        int icon = t.equals("OK") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(p, m, t, icon);
    }
}