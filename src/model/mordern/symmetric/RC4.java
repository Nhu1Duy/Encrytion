package model.mordern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class RC4 implements SymmetricCipher {

	private SecretKey secretKey;
	private final String engineName = "ARCFOUR";
	private final String config = "ARCFOUR";
	private int bitLen = 128;

	public void setKeySize(int val) {
		if (val < 40 || val > 1024 || val % 8 != 0) {
			throw new IllegalArgumentException("Sai kích thước key RC4: " + val + ". Phải nằm trong khoảng 40–1024 và chia hết cho 8!");
		}
		this.bitLen = val;
	}

	@Override
	public void setTransformation(String m, String p) {
	}

	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance(engineName);
		generator.init(bitLen, new SecureRandom());
		this.secretKey = generator.generateKey();

		return this.secretKey;
	}

	@Override
	public void loadKey(SecretKey k) {
		this.secretKey = k;
	}

	@Override
	public IvParameterSpec genIV() {
		return null;
	}

	@Override
	public void loadIV(IvParameterSpec iv) {
	}

	private Cipher getCipher(int mode) throws Exception {
		Cipher c = Cipher.getInstance(config);
		c.init(mode, secretKey);

		return c;
	}

	@Override
	public String encryptBase64(String input) throws Exception {
		byte[] rawOut = getCipher(Cipher.ENCRYPT_MODE).doFinal(input.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(rawOut);
	}

	@Override
	public String decryptBase64(String base64Str) throws Exception {
		byte[] rawIn = Base64.getDecoder().decode(base64Str);
		byte[] origin = getCipher(Cipher.DECRYPT_MODE).doFinal(rawIn);
		return new String(origin, StandardCharsets.UTF_8);
	}

	@Override
	public boolean processFile(String src, String target, boolean isEncrypt) throws Exception {
		File fIn = new File(src);
		File fOut = new File(target);

		try (InputStream input = new BufferedInputStream(new FileInputStream(fIn));
				OutputStream output = new BufferedOutputStream(new FileOutputStream(fOut))) {
			Cipher cipher = getCipher(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE);
			executeStream(input, output, cipher);
		}

		return true;
	}

	private void executeStream(InputStream is, OutputStream os, Cipher c) throws Exception {
		byte[] cache = new byte[8192];
		int read;

		while ((read = is.read(cache)) != -1) {
			byte[] processed = c.update(cache, 0, read);
			if (processed != null) {
				os.write(processed);
			}
		}

		byte[] last = c.doFinal();
		if (last != null) {
			os.write(last);
		}

		os.flush();
	}

	public String getTransformation() {
		return config;
	}

	public int getKeySize() {
		return bitLen;
	}
}