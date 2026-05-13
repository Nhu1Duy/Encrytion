package model.mordern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class RC4 implements SymmetricCipher {

	private SecretKey key;
	private String algorithm = "ARCFOUR";
	private String transformation = "ARCFOUR";

	/**
	 * RC4 là stream cipher → KHÔNG có IV, KHÔNG có padding, KHÔNG có mode.
	 *
	 * KEY SIZE hỗ trợ: 40 – 1024 bit (bội số của 8)
	 */
	private int keySize = 128;

	// =========================
	// CHỌN KEY SIZE
	// =========================
	public void setKeySize(int keySize) {
		if (keySize < 40 || keySize > 1024 || keySize % 8 != 0) {
			throw new IllegalArgumentException("RC4 key size phải trong khoảng 40–1024 bit và là bội số của 8.");
		}
		this.keySize = keySize;
	}

	// =========================
	// SET MODE + PADDING
	// =========================
	@Override
	public void setTransformation(String mode, String padding) {
		System.out.println("[RC4] setTransformation() bị bỏ qua: RC4 chỉ hỗ trợ ARCFOUR.");
	}

	// =========================
	// TẠO KHÓA
	// =========================
	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("ARCFOUR");
		keyGenerator.init(keySize, new SecureRandom());
		key = keyGenerator.generateKey();
		return key;
	}

	@Override
	public void loadKey(SecretKey key) {
		this.key = key;
	}

	// =========================
	// IV
	// =========================
	@Override
	public IvParameterSpec genIV() {
		System.out.println("[RC4] genIV() bị bỏ qua: RC4 là stream cipher, không dùng IV.");
		return null;
	}

	@Override
	public void loadIV(IvParameterSpec iv) {
		System.out.println("[RC4] loadIV() bị bỏ qua: RC4 là stream cipher, không dùng IV.");
	}

	// =========================
	// KHỞI TẠO CIPHER
	// =========================
	private Cipher initCipher(int mode) throws Exception {
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(mode, key);
		return cipher;
	}

	// =========================
	// MÃ HÓA TEXT -> BASE64
	// =========================
	@Override
	public String encryptBase64(String plainText) throws Exception {
		Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
		byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	// =========================
	// GIẢI MÃ BASE64 -> TEXT
	// =========================
	@Override
	public String decryptBase64(String encryptedText) throws Exception {
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
		Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
		byte[] decrypted = cipher.doFinal(encryptedBytes);
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	// =========================
	// MÃ HÓA / GIẢI MÃ FILE
	// =========================
	@Override
	public boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception {
		// RC4 không có IV, không có header — đọc/ghi thẳng vào destFile
		try (
			BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(sourceFile));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))
		) {
			Cipher cipher = initCipher(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE);
			byte[] buffer = new byte[4096];
			int n;
			while ((n = bis.read(buffer)) != -1) {
				byte[] chunk = cipher.update(buffer, 0, n);
				if (chunk != null) bos.write(chunk);
			}
			byte[] finalOut = cipher.doFinal();
			if (finalOut != null) bos.write(finalOut);
		}
		return true;
	}

	// =========================
	// GETTER THÔNG TIN
	// =========================
	public String getTransformation() {
		return transformation;
	}

	public int getKeySize() {
		return keySize;
	}
}