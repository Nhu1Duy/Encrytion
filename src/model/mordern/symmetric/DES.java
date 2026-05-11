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

public class DES implements SymmetricCipher {

	private SecretKey key;
	private IvParameterSpec iv;
	private String algorithm = "DES";
	private String transformation = "DES/CBC/PKCS5Padding";

	private int keySize = 56;

	// =========================
	// SET MODE + PADDING
	// =========================
	@Override
	public void setTransformation(String mode, String padding) {
		if (mode.isEmpty() || padding.isEmpty()) {
			this.transformation = this.algorithm;
		} else {
			this.transformation = this.algorithm + "/" + mode + "/" + padding;
		}
	}

	// =========================
	// TẠO KHÓA DES
	// =========================
	@Override
	public SecretKey genKey() throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
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
		iv = new IvParameterSpec(new byte[8]);
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

		if (transformation.equals("ARCFOUR") || transformation.contains("ECB")) {
			cipher.init(mode, key);
		} else {
			if (iv == null) {
				genIV();
			}

			IvParameterSpec ivSpec = iv;
			cipher.init(mode, key, ivSpec);
		}

		return cipher;
	}

	// =========================
	// MÃ HÓA TEXT -> BASE64
	// =========================
	@Override
	public String encryptBase64(String plainText) throws Exception {

		Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);

		byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
		byte[] encrypted = cipher.doFinal(data);
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

		int mode;

		if (encrypt) {
			mode = Cipher.ENCRYPT_MODE;
		} else {
			mode = Cipher.DECRYPT_MODE;
		}

		Cipher cipher = initCipher(mode);

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {
			byte[] buffer = new byte[1024];

			int readBytes;

			while ((readBytes = bis.read(buffer)) != -1) {
				byte[] output = cipher.update(buffer, 0, readBytes);
				if (output != null) {
					bos.write(output);
				}
			}

			byte[] finalOutput = cipher.doFinal();

			if (finalOutput != null) {
				bos.write(finalOutput);
			}

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