package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class FileManager {

    private static final String TXT_DESC  = "Text files (*.txt)";
    private static final String KEY_DESC  = "Key files (*.key, *.txt)";
    private static final String[] TXT_EXT = {"txt"};
    private static final String[] KEY_EXT = {"key", "txt"};

    public static String importText(Component parent) {
        File file = chooseOpenFile(parent, TXT_DESC, TXT_EXT);
        if (file == null) return null;
        return readFile(parent, file);
    }

    public static boolean saveText(Component parent, String content) {
        File file = chooseSaveFile(parent, TXT_DESC, TXT_EXT, "output.txt");
        if (file == null) return false;
        return writeFile(parent, file, content);
    }


    public static String importKey(Component parent) {
        File file = chooseOpenFile(parent, KEY_DESC, KEY_EXT);
        if (file == null) return null;
        return readFile(parent, file);
    }


    public static boolean saveKey(Component parent, String content) {
        File file = chooseSaveFile(parent, KEY_DESC, KEY_EXT, "secret.key");
        if (file == null) return false;
        return writeFile(parent, file, content);
    }

    /**
     * Mở dialog chọn file bất kỳ (không filter định dạng)
     */
    public static File chooseOpenFile(Component parent, String desc, String[] extensions) {
        JFileChooser fc = new JFileChooser();
        
        if (extensions != null && extensions.length > 0) {
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new FileNameExtensionFilter(desc, extensions));
        } else {
            fc.setAcceptAllFileFilterUsed(true);
        }
        
        int result = fc.showOpenDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    /**
     * Mở dialog chọn thư mục
     */
    public static File chooseDirectory(Component parent, File initialDirectory) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Chọn thư mục lưu file output");
        
        if (initialDirectory != null && initialDirectory.exists()) {
            fc.setCurrentDirectory(initialDirectory);
        }
        
        int result = fc.showOpenDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    /**
     * Tạo tên file output dựa trên tên file input
     * Nếu file input kết thúc bằng ".enc" thì bỏ extension
     * Ngược lại thêm ".enc" vào cuối
     */
    public static String generateOutputFileName(String inputFilePath) {
        if (inputFilePath == null || inputFilePath.trim().isEmpty()) {
            return "output.enc";
        }
        
        String inputName = new File(inputFilePath).getName();
        return inputName.endsWith(".enc") ? 
            inputName.substring(0, inputName.length() - 4) : 
            inputName + ".enc";
    }

    /**
     * Tạo đường dẫn output đầy đủ
     */
    public static String buildOutputPath(String directory, String inputFilePath) {
        String outputFileName = generateOutputFileName(inputFilePath);
        return directory + File.separator + outputFileName;
    }

    private static File chooseSaveFile(Component parent, String desc, String[] extensions, String defaultName) {
        JFileChooser fc = buildChooser(desc, extensions);
        fc.setSelectedFile(new File(defaultName));
        int result = fc.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return null;

        File file = fc.getSelectedFile();
        String name  = file.getName();
        boolean hasExt = false;
        for (String ext : extensions) {
            if (name.toLowerCase().endsWith("." + ext)) { hasExt = true; break; }
        }
        if (!hasExt) file = new File(file.getParentFile(), name + "." + extensions[0]);

        if (file.exists()) {
            int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "File \"" + file.getName() + "\" đã tồn tại.\nBạn có muốn ghi đè không?",
                    "Xác nhận ghi đè",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return null;
        }
        return file;
    }

    private static JFileChooser buildChooser(String desc, String[] extensions) {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(desc, extensions));
        return fc;
    }

    private static String readFile(Component parent, File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Không thể đọc file:\n" + ex.getMessage(),
                    "Lỗi đọc file", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static boolean writeFile(Component parent, File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(parent,
                    "Đã lưu thành công:\n" + file.getAbsolutePath(),
                    "Lưu file", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Không thể lưu file:\n" + ex.getMessage(),
                    "Lỗi lưu file", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}