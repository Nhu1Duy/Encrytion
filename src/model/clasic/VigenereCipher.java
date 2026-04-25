package model.clasic;

import Tool.Alphabet;
import java.util.Random;

/**
 * Vigenere Cipher – mỗi ký tự được dịch vòng theo ký tự tương ứng trong key
 */
public class VigenereCipher implements ClassicCipher {
	/// --- GEN KEY ---
	public static String genKey(String alphabet, int length) {
		if (length <= 0)
			return "";
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = rand.nextInt(alphabet.length());
			sb.append(alphabet.charAt(index));
		}
		return sb.toString();
	}

	/// --- Implement ---
	@Override
	public String encryptEN(String plainText, String key) {
		return process(plainText, key, true, Alphabet.EN_ALPHABET_FUL);
	}

	@Override
	public String decryptEN(String cipherText, String key) {
		return process(cipherText, key, false, Alphabet.EN_ALPHABET_FUL);
	}

	@Override
	public String encryptVN(String plainText, String key) {
		return process(plainText, key, true, Alphabet.VN_ALPHABET_FUL);
	}

	@Override
	public String decryptVN(String cipherText, String key) {
		return process(cipherText, key, false, Alphabet.VN_ALPHABET_FUL);
	}

	/// --- Handle ---
	private String process(String text, String key, boolean encrypt, String alphabet) {
		int m = alphabet.length();
		int kj = 0; 
		StringBuilder sb = new StringBuilder(text.length());

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			int p = alphabet.indexOf(c);
			if (p >= 0) {
				char keyChar = key.charAt(kj);
				int k = alphabet.indexOf(keyChar);

				if (k < 0) {
					k = 0; 
				}
				int cy;
				if (encrypt) {
					cy = (p + k) % m;
				} else {
					cy = (p - k) % m;
					if (cy < 0) {
						cy = cy + m;
					}
				}
				sb.append(alphabet.charAt(cy));
				kj = kj + 1;
				if (kj == key.length()) {
					kj = 0;
				}

			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

}
