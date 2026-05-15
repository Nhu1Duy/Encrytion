package model.mordern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AES implements SymmetricCipher {

	private SecretKey secretKey;
	private IvParameterSpec initVector;
	private String baseAlgo = "AES";
	private String currentTransform = "AES/CBC/PKCS5Padding";
	private int currentKeyBits = 128;

	public void setKeySize(int size) {
		boolean isValid = (size == 128 || size == 192 || size == 256);

		if (!isValid) {
			throw new IllegalArgumentException("Sai kích thước key AES: " + size + ". Chỉ chấp nhận 128, 192 hoặc 256 bits!");
		}

		this.currentKeyBits = size;
	}

	@Override
	public void setTransformation(String mode, String padding) {
		StringBuilder sb = new StringBuilder(baseAlgo);
		if (mode == null || mode.trim().isEmpty()) {
			this.currentTransform = baseAlgo;
			return;
		}

		if ("GCM".equalsIgnoreCase(mode)) {
			this.currentTransform = "AES/GCM/NoPadding";
		} else {
			String finalPadding = (padding == null || padding.isEmpty()) ? "PKCS5Padding" : padding;
			this.currentTransform = sb.append("/").append(mode).append("/").append(finalPadding).toString();
		}
	}

	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator kGen = KeyGenerator.getInstance(baseAlgo);
		kGen.init(this.currentKeyBits, new SecureRandom());
		this.secretKey = kGen.generateKey();
		return this.secretKey;
	}

	@Override
	public void loadKey(SecretKey key) {
		this.secretKey = key;
	}

	@Override
	public IvParameterSpec genIV() {
		int size = currentTransform.contains("GCM") ? 12 : 16;
		byte[] randomBytes = new byte[size];
		new SecureRandom().nextBytes(randomBytes);
		this.initVector = new IvParameterSpec(randomBytes);

		return this.initVector;
	}

	@Override
	public void loadIV(IvParameterSpec iv) {
		this.initVector = iv;
	}

	private Cipher setupCipher(int opMode) throws Exception {
		Cipher instance = Cipher.getInstance(currentTransform);
		if (currentTransform.contains("/ECB/")) {
			instance.init(opMode, secretKey);

		} else if (currentTransform.contains("/GCM/")) {
			if (initVector == null) {
				genIV();
			}

			instance.init(opMode, secretKey, new GCMParameterSpec(128, initVector.getIV()));
		} else {

			if (initVector == null) {
				genIV();
			}
			instance.init(opMode, secretKey, initVector);
		}

		return instance;
	}

	@Override
	public String encryptBase64(String plain) throws Exception {
		Cipher c = setupCipher(Cipher.ENCRYPT_MODE);
		byte[] rawOut = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(rawOut);
	}

	@Override
	public String decryptBase64(String cipherText) throws Exception {
		byte[] rawIn = Base64.getDecoder().decode(cipherText);
		Cipher c = setupCipher(Cipher.DECRYPT_MODE);
		return new String(c.doFinal(rawIn), StandardCharsets.UTF_8);
	}

	@Override
	public boolean processFile(String src, String target, boolean isEncrypt) throws Exception {
		if (isEncrypt) {
			return performEncryption(new File(src), new File(target));
		} else {
			return performDecryption(new File(src), new File(target));
		}
	}

	private boolean performEncryption(File source, File destination) throws Exception {
		try (InputStream in = new BufferedInputStream(new FileInputStream(source));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(destination))) {
			if (!currentTransform.contains("ECB")) {
				genIV();
				out.write(initVector.getIV());
			}
			Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE);
			processStream(in, out, cipher);
		}

		return true;
	}

	private boolean performDecryption(File source, File destination) throws Exception {
		try (InputStream in = new BufferedInputStream(new FileInputStream(source))) {
			if (!currentTransform.contains("ECB")) {
				int expectedIvSize = currentTransform.contains("GCM") ? 12 : 16;
				byte[] ivHeader = new byte[expectedIvSize];
				if (in.read(ivHeader) < expectedIvSize) {
					throw new IOException("File AES không hợp lệ: thiếu IV!");
				}
				this.initVector = new IvParameterSpec(ivHeader);
			}

			Cipher cipher = setupCipher(Cipher.DECRYPT_MODE);
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destination))) {
				processStream(in, out, cipher);
			}
		}

		return true;
	}

	private void processStream(InputStream is, OutputStream os, Cipher c) throws Exception {
		byte[] buffer = new byte[8192];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			byte[] output = c.update(buffer, 0, bytesRead);
			if (output != null) {
				os.write(output);
			}
		}
		byte[] finalBlock = c.doFinal();

		if (finalBlock != null) {
			os.write(finalBlock);
		}

		os.flush();
	}

	public String getTransformation() {
		return currentTransform;
	}

	public int getKeySize() {
		return currentKeyBits;
	}
}