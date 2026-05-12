package model.mordern.symmetric;

import javax.crypto.Cipher;
import util.HeaderManager;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.spec.GCMParameterSpec;

public class AES implements SymmetricCipher {

	private SecretKey key;
	private IvParameterSpec iv;
	private String algorithm = "AES";
	private String transformation = "AES/CBC/PKCS5Padding";

	/**
	 * KEY SIZE hỗ trợ: 128, 192, 256 bit
	 */
	private int keySize = 128;

	// =========================
	// CHỌN KEY SIZE
	// =========================
	public void setKeySize(int keySize) {
		if (keySize != 128 && keySize != 192 && keySize != 256) {
			throw new IllegalArgumentException("AES key size phải là 128, 192 hoặc 256 bit.");
		}
		this.keySize = keySize;
	}

	// =========================
	// SET MODE + PADDING
	// =========================
	@Override
	public void setTransformation(String mode, String padding) {
		if (mode == null || mode.isEmpty()) {
			this.transformation = this.algorithm;
		} else if (mode.equalsIgnoreCase("GCM")) {
			this.transformation = this.algorithm + "/GCM/NoPadding";
		} else if (padding == null || padding.isEmpty()) {
			this.transformation = this.algorithm + "/" + mode + "/PKCS5Padding";
		} else {
			this.transformation = this.algorithm + "/" + mode + "/" + padding;
		}
	}

	// =========================
	// TẠO KHÓA AES
	// =========================
	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(keySize, new SecureRandom());
		key = keyGenerator.generateKey();
		return key;
	}

	@Override
	public void loadKey(SecretKey key) {
		this.key = key;
	}

	// =========================
	// TẠO IV
	// =========================
	@Override
	public IvParameterSpec genIV() {
		byte[] ivBytes = new byte[transformation.contains("GCM") ? 12 : 16];
		new SecureRandom().nextBytes(ivBytes);
		iv = new IvParameterSpec(ivBytes);
		return iv;
	}

	@Override
	public void loadIV(IvParameterSpec iv) {
		this.iv = iv;
	}

	// =========================
	// KHỞI TẠO CIPHER
	// =========================
	private Cipher initCipher(int mode) throws Exception {
		Cipher cipher = Cipher.getInstance(transformation);

		if (transformation.contains("ECB")) {
			cipher.init(mode, key);
		} else if (transformation.contains("GCM")) {
			if (iv == null) genIV();
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv.getIV());
			cipher.init(mode, key, gcmSpec);
		} else {
			if (iv == null) genIV();
			cipher.init(mode, key, iv);
		}

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
		if (encrypt) {
			// ── ENCRYPT: ghi header + IV vào đầu file, rồi ghi ciphertext ──────
			try (
				BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(sourceFile));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))
			) {
				if (!transformation.contains("ECB")) {
					HeaderManager.writeHeader(bos, new File(sourceFile).getName());
					genIV();
					bos.write(iv.getIV());
				} else {
					HeaderManager.writeHeader(bos, new File(sourceFile).getName());
				}
				Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
				byte[] buffer = new byte[4096];
				int n;
				while ((n = bis.read(buffer)) != -1) {
					byte[] chunk = cipher.update(buffer, 0, n);
					if (chunk != null) bos.write(chunk);
				}
				byte[] finalOut = cipher.doFinal();
				if (finalOut != null) bos.write(finalOut);
			}
		} else {
			// ── DECRYPT: đọc header + IV từ file nguồn, ghi plaintext ra file đúng tên ──
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
				// 1. Đọc header → lấy tên file gốc
				String realDest = HeaderManager.readHeader(bis, destFile);

				// 2. Đọc IV (trừ ECB)
				if (!transformation.contains("ECB")) {
					int ivLen = transformation.contains("GCM") ? 12 : 16;
					byte[] ivBytes = new byte[ivLen];
					int total = 0;
					while (total < ivLen) {
						int n = bis.read(ivBytes, total, ivLen - total);
						if (n == -1) throw new Exception("File bị hỏng: không đọc được IV.");
						total += n;
					}
					iv = new IvParameterSpec(ivBytes);
				}

				// 3. Giải mã vào file tên gốc
				Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
				try (BufferedOutputStream out =
						new BufferedOutputStream(new FileOutputStream(realDest))) {
					byte[] buffer = new byte[4096];
					int n;
					while ((n = bis.read(buffer)) != -1) {
						byte[] chunk = cipher.update(buffer, 0, n);
						if (chunk != null) out.write(chunk);
					}
					byte[] finalOut = cipher.doFinal();
					if (finalOut != null) out.write(finalOut);
				}
			}
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