package model.mordern.hash;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public abstract class AbstractHash implements HashFunction {

	protected abstract String getAlgorithm();

	@Override
	public String hashString(String text) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(getAlgorithm(), "BC");
			byte[] hashedBytes = messageDigest.digest(text.getBytes());
			
			BigInteger decimalValue = new BigInteger(1, hashedBytes);
			return decimalValue.toString(16);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String hashFile(String path) throws Exception {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(getAlgorithm(), "BC");
			InputStream input = new BufferedInputStream(new FileInputStream(path));
			
			DigestInputStream digestStream = new DigestInputStream(input, messageDigest);
			
			byte[] dataBlock = new byte[1024];
			while (digestStream.read(dataBlock) != -1) {
			}
			
			digestStream.close();
			input.close();

			BigInteger decimalValue = new BigInteger(1, messageDigest.digest());
			return decimalValue.toString(16);

		} catch (Exception ex) {
			throw new Exception(ex);
		}
	}

	@Override
	public String getAlgorithmName() {
		return getAlgorithm();
	}
}