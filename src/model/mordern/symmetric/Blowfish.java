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

public class Blowfish implements SymmetricCipher {

	private SecretKey key;
	private IvParameterSpec iv;
	private String algorithm = "Blowfish";
	private String transformation = "Blowfish/CBC/PKCS5Padding";

	/**
	 * KEY SIZE hỗ trợ: 128, 192, 256 bit
	 */
	private int keySize = 128;

	// =========================
	// CHỌN KEY SIZE
	// =========================
	public void setKeySize(int keySize) {
		if (keySize < 32 || keySize > 448 || keySize % 8 != 0) {
			throw new IllegalArgumentException("Blowfish key size phải trong khoảng 32–448 bit và là bội số của 8.");
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
		} else if (padding == null || padding.isEmpty()) {
			this.transformation = this.algorithm + "/" + mode + "/PKCS5Padding";
		} else {
			this.transformation = this.algorithm + "/" + mode + "/" + padding;
		}
	}

	// =========================
	// TẠO KHÓA
	// =========================
	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
		keyGenerator.init(keySize, new SecureRandom());
		key = keyGenerator.generateKey();
		return key;
	}

	@Override
	public void loadKey(SecretKey key) {
		this.key = key;
	}

	// =========================
	// TẠO IV (8 byte cho Blowfish)
	// =========================
	@Override
	public IvParameterSpec genIV() {
		byte[] ivBytes = new byte[8];
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
			try (
				BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(sourceFile));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))
			) {
				if (!transformation.contains("ECB")) {
					genIV();
					bos.write(iv.getIV());
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
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
				if (!transformation.contains("ECB")) {
					byte[] ivBytes = new byte[8];
					int total = 0;
					while (total < 8) {
						int n = bis.read(ivBytes, total, 8 - total);
						if (n == -1) throw new Exception("File bị hỏng: không đọc được IV.");
						total += n;
					}
					iv = new IvParameterSpec(ivBytes);
				}

				Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
				try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile))) {
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