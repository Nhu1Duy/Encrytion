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
		byte[] ivBytes;
		if (transformation.contains("GCM")) {
			ivBytes = new byte[12];
		} else {
			ivBytes = new byte[16];
		}
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
		int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
		Cipher cipher = initCipher(mode);

		try (
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {

			byte[] buffer = new byte[1024];
			int readBytes;

			while ((readBytes = bis.read(buffer)) != -1) {
				byte[] output = cipher.update(buffer, 0, readBytes);
				if (output != null)
					bos.write(output);
			}

			byte[] finalOutput = cipher.doFinal();
			if (finalOutput != null)
				bos.write(finalOutput);

			return true;
		}
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