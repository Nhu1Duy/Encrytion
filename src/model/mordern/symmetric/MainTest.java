package model.mordern.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.util.Base64;

public class MainTest {
    public static void main(String[] args) {
        try {
            DES des = new DES();
            
            // 1. Thiết lập cấu hình (Người dùng chọn trên UI)
            // Bạn có thể thử đổi thành "CTR", "NoPadding" để test tính linh hoạt
            des.setTransformation("CBC", "PKCS5Padding"); 
            System.out.println("--- Configuration ---");
            System.out.println("Algorithm: DES");

            // 2. Tạo Khóa và IV
            System.out.println("\n--- 1. Testing Key & IV Generation ---");
            SecretKey key = des.genKey();
            IvParameterSpec iv = des.genIV();
            System.out.println("Key generated!");
            System.out.println("IV generated (8 bytes of zeros): " + Base64.getEncoder().encodeToString(iv.getIV()));

            // 3. Kiểm tra mã hóa/giải mã Văn bản (Base64)
            System.out.println("\n--- 2. Testing String Encryption (Base64) ---");
            String originalText = "Chào bạn, đây là thông tin bí mật!";
            
            // Mã hóa
            String encryptedBase64 = des.encryptBase64(originalText);
            
            // Giải mã
            String decryptedText = des.decryptBase64(encryptedBase64);

            System.out.println("Original: " + originalText);
            System.out.println("Encrypted (Base64): " + encryptedBase64);
            System.out.println("Decrypted: " + decryptedText);

            // 4. Kiểm tra mã hóa/giải mã File
            System.out.println("\n--- 3. Testing File Encryption/Decryption ---");
            
            String sourceFile = "E:\\2026Learn\\ATVBMHTTT\\slide_baigiang_ATBMHTTT.zip";
            String encryptedFile = "E:\\2026Learn\\ATVBMHTTT\\slide_baigiang_ATBMHTTT.zip.enc";
            String decryptedFile = "E:\\2026Learn\\ATVBMHTTT\\slide_baigiang_ATBMHTTT_RECOVERED.zip";

            File file = new File(sourceFile);
            if (!file.exists()) {
                System.out.println("Lỗi: Không tìm thấy file nguồn tại: " + sourceFile);
            } else {
                // Thực hiện mã hóa file (encrypt = true)
                System.out.println("Encrypting file...");
                boolean isEncrypted = des.processFile(sourceFile, encryptedFile, true);
                if (isEncrypted) System.out.println("File encrypted to: " + encryptedFile);

                // Thực hiện giải mã file (encrypt = false)
                System.out.println("Decrypting file...");
                boolean isDecrypted = des.processFile(encryptedFile, decryptedFile, false);
                if (isDecrypted) System.out.println("File decrypted to: " + decryptedFile);
            }

            System.out.println("\n=> TẤT CẢ CÁC BÀI TEST ĐÃ HOÀN TẤT!");

        } catch (Exception e) {
            System.err.println("Đã xảy ra lỗi trong quá trình test:");
            e.printStackTrace();
        }
    }
}