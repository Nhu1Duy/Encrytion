package model.mordern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Blowfish implements SymmetricCipher {

	private SecretKey secretKey;
	private IvParameterSpec vector;
	private String mainAlgo = "Blowfish";
	private String currentConfig = "Blowfish/CBC/PKCS5Padding";
	private int keyBits = 128;

	public void setKeySize(int bits) {
		if (bits < 32 || bits > 448 || bits % 8 != 0) {
			throw new IllegalArgumentException(
					"Sai kích thước key Blowfish: " + bits + ". Phải nằm trong khoảng 32–448 và chia hết cho 8!");
		}
		this.keyBits = bits;
	}

	@Override
	public void setTransformation(String mode, String padding) {
		if (mode == null || mode.isEmpty()) {
			this.currentConfig = this.mainAlgo;
		} else {
			String p = (padding == null || padding.isEmpty()) ? "PKCS5Padding" : padding;
			this.currentConfig = String.format("%s/%s/%s", this.mainAlgo, mode, p);
		}
	}

	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(mainAlgo);
		kg.init(keyBits, new SecureRandom());
		this.secretKey = kg.generateKey();

		return this.secretKey;
	}

	@Override
	public void loadKey(SecretKey k) {
		this.secretKey = k;
	}

	@Override
	public IvParameterSpec genIV() {
		byte[] rawIv = new byte[8];
		new SecureRandom().nextBytes(rawIv);
		this.vector = new IvParameterSpec(rawIv);

		return this.vector;
	}

	@Override
	public void loadIV(IvParameterSpec ivSpec) {
		this.vector = ivSpec;
	}

	private Cipher prepareCipher(int opMode) throws Exception {
		Cipher c = Cipher.getInstance(currentConfig);
		if (currentConfig.contains("/ECB/")) {
			c.init(opMode, secretKey);
		} else {
			if (this.vector == null) {
				genIV();
			}

			c.init(opMode, secretKey, vector);
		}

		return c;
	}

	@Override
	public String encryptBase64(String text) throws Exception {
		Cipher c = prepareCipher(Cipher.ENCRYPT_MODE);
		byte[] out = c.doFinal(text.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(out);
	}

	@Override
	public String decryptBase64(String code) throws Exception {
		byte[] raw = Base64.getDecoder().decode(code);
		Cipher c = prepareCipher(Cipher.DECRYPT_MODE);
		return new String(c.doFinal(raw), StandardCharsets.UTF_8);
	}

	@Override
	public boolean processFile(String input, String output, boolean isEncrypt) throws Exception {
		File src = new File(input);
		File dst = new File(output);
		if (isEncrypt) {
			encryptFile(src, dst);
		} else {
			decryptFile(src, dst);
		}

		return true;
	}

	private void encryptFile(File fIn, File fOut) throws Exception {
		try (InputStream is = new BufferedInputStream(new FileInputStream(fIn));
				OutputStream os = new BufferedOutputStream(new FileOutputStream(fOut))) {
			if (!currentConfig.contains("/ECB/")) {
				genIV();
				os.write(this.vector.getIV());
			}

			Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE);
			handleDataStream(is, os, cipher);
		}
	}

	private void decryptFile(File fIn, File fOut) throws Exception {
		try (InputStream is = new BufferedInputStream(new FileInputStream(fIn))) {
			if (!currentConfig.contains("/ECB/")) {
				byte[] header = new byte[8];
				if (is.read(header) < 8) {
					throw new IOException("File Blowfish không hợp lệ: thiếu IV!");
				}
				this.vector = new IvParameterSpec(header);
			}

			Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE);
			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fOut))) {
				handleDataStream(is, os, cipher);
			}
		}
	}

	private void handleDataStream(InputStream in, OutputStream out, Cipher c) throws Exception {
		byte[] tmp = new byte[8192];
		int r;
		while ((r = in.read(tmp)) != -1) {
			byte[] block = c.update(tmp, 0, r);
			if (block != null) {
				out.write(block);
			}
		}

		byte[] finalPart = c.doFinal();
		if (finalPart != null) {
			out.write(finalPart);
		}

		out.flush();
	}

	public String getTransformation() {
		return currentConfig;
	}

	public int getKeySize() {
		return keyBits;
	}
}