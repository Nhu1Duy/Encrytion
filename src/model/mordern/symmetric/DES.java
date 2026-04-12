package model.mordern.symmetric;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;

public class DES {
	SecretKey key;
	public SecretKey genKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(56);
		key = keyGenerator.generateKey();
		return  key;
	}
	
	public void loadKey(SecretKey key) {
		this.key = key;
	}
	
	public byte[] encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		byte[] data = text.getBytes(StandardCharsets.UTF_8);
		return cipher.doFinal(data);
	}

//	private byte[] expand(byte[] data, byte[] expand, int limit) {
//		
//	}
	
	public String encryptBase64(String text) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return Base64.getEncoder().encodeToString(encrypt(text));
	}
	
	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
		byte[] bytes = cipher.doFinal(data);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
//	public String decryptBase64(String data) {
//		
//	}
//	
	
	public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des));
		CipherInputStream in = new CipherInputStream(input, cipher);
		int i;
		byte[] read = new byte[1024];
		byte[] re = null;
		while((i = in.read(read)) != -1) {
			out.write(read, 0, i);
		}
		read = cipher.doFinal();
		if(read != null) {
			out.write(read);
		}
		in.close();
		out.flush();
		out.close();
		return true;
	}
	
	public boolean decryptFile(String src, String des)  throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des));
		CipherOutputStream output = new CipherOutputStream(out, cipher);
		int i;
		byte[] read = new byte[1024];
		byte[] re = null;
		while((i = input.read(read)) != -1) {
			output.write(read, 0, i);
		}
		read = cipher.doFinal();
		if(read != null) {
			output.write(read);
		}
		input.close();
		out.flush();
		out.close();
		return true;
	}
}
