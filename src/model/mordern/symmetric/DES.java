package model.mordern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class DES implements SymmetricCipher {

	private SecretKey secretKey;
	private IvParameterSpec initVector;
	private final String algoName = "DES";
	private String transformConfig = "DES/CBC/PKCS5Padding";
	private final int bitLength = 56;

	@Override
	public void setTransformation(String mode, String padding) {
		if (mode == null || mode.isEmpty() || padding == null || padding.isEmpty()) {
			this.transformConfig = algoName;
		} else {
			StringBuilder builder = new StringBuilder();
			this.transformConfig = builder.append(algoName).append("/").append(mode).append("/").append(padding).toString();
		}
	}

	@Override
	public SecretKey genKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(algoName);
		kg.init(bitLength, new SecureRandom());
		this.secretKey = kg.generateKey();

		return this.secretKey;
	}

	@Override
	public void loadKey(SecretKey k) {
		this.secretKey = k;
	}

	@Override
	public IvParameterSpec genIV() {
		byte[] raw = new byte[8];
		new SecureRandom().nextBytes(raw);
		this.initVector = new IvParameterSpec(raw);

		return this.initVector;
	}

	@Override
	public void loadIV(IvParameterSpec ivSpec) {
		this.initVector = ivSpec;
	}

	private Cipher getCipherInstance(int opMode) throws Exception {
		Cipher instance = Cipher.getInstance(transformConfig);
		if (transformConfig.equals("ARCFOUR") || transformConfig.contains("/ECB/")) {
			instance.init(opMode, secretKey);
		} else {
			if (this.initVector == null) {
				genIV();
			}
			instance.init(opMode, secretKey, initVector);
		}

		return instance;
	}

	@Override
	public String encryptBase64(String input) throws Exception {
		Cipher c = getCipherInstance(Cipher.ENCRYPT_MODE);
		byte[] rawOutput = c.doFinal(input.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(rawOutput);
	}

	@Override
	public String decryptBase64(String encoded) throws Exception {
		byte[] rawInput = Base64.getDecoder().decode(encoded);
		Cipher c = getCipherInstance(Cipher.DECRYPT_MODE);

		return new String(c.doFinal(rawInput), StandardCharsets.UTF_8);
	}

	@Override
	public boolean processFile(String srcPath, String destPath, boolean encryptMode) throws Exception {
		File inputFile = new File(srcPath);
		File outputFile = new File(destPath);
		if (encryptMode) {
			executeFileEncryption(inputFile, outputFile);
		} else {
			executeFileDecryption(inputFile, outputFile);
		}

		return true;
	}

	private void executeFileEncryption(File input, File output) throws Exception {
		try (InputStream is = new BufferedInputStream(new FileInputStream(input));
				OutputStream os = new BufferedOutputStream(new FileOutputStream(output))) {
			if (!transformConfig.contains("/ECB/")) {
				genIV();
				os.write(initVector.getIV());
			}
			Cipher cipher = getCipherInstance(Cipher.ENCRYPT_MODE);

			transferData(is, os, cipher);
		}
	}

	private void executeFileDecryption(File input, File output) throws Exception {
		try (InputStream is = new BufferedInputStream(new FileInputStream(input))) {
			if (!transformConfig.contains("/ECB/")) {
				byte[] ivHeader = new byte[8];

				if (is.read(ivHeader) < 8) {
					throw new IOException("File DES không hợp lệ: thiếu IV!");
				}

				this.initVector = new IvParameterSpec(ivHeader);
			}
			Cipher cipher = getCipherInstance(Cipher.DECRYPT_MODE);
			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(output))) {
				transferData(is, os, cipher);
			}
		}
	}

	private void transferData(InputStream in, OutputStream out, Cipher c) throws Exception {
		byte[] dataBuffer = new byte[8192];
		int readCount;
		while ((readCount = in.read(dataBuffer)) != -1) {

			byte[] processed = c.update(dataBuffer, 0, readCount);

			if (processed != null) {
				out.write(processed);
			}
		}
		byte[] finalBlock = c.doFinal();

		if (finalBlock != null) {
			out.write(finalBlock);
		}

		out.flush();
	}

	public String getTransformation() {
		return transformConfig;
	}

	public int getKeySize() {
		return bitLength;
	}
}