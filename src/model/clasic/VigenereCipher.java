package model.clasic;

import Tool.Alphabet;
import  java.util.Random;

public class VigenereCipher {
	public String genKey(String alphabet, int length) {
		if (length <= 0) return "";
		Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

	public String encryptEN(String input, String key) {
		
		StringBuilder output = new StringBuilder();
		int m = Alphabet.EN_ALPHABET_FUL.length();
		int j = 0;

		for (int i = 0; i < input.length(); i++) {
			char iInput = input.charAt(i);
			char jInput = key.charAt(j);
			int p = Alphabet.EN_ALPHABET_FUL.indexOf(iInput);
			int k = Alphabet.EN_ALPHABET_FUL.indexOf(jInput);
			if (p != -1) {
				if (k == -1) k = 0;
				int cy = (p + k) % m;
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(cy));
				j = (j + 1) % key.length();
			} else {
				output.append(iInput);
			}
		}

		return output.toString();
	}
	public String decryptEN(String input, String key) {
		StringBuilder output = new StringBuilder();
		int m = Alphabet.EN_ALPHABET_FUL.length();
		int j = 0;

		for (int i = 0; i < input.length(); i++) {
			char iInput = input.charAt(i);
			char jInput = key.charAt(j);
			int p = Alphabet.EN_ALPHABET_FUL.indexOf(iInput);
			int k = Alphabet.EN_ALPHABET_FUL.indexOf(jInput);
			if (p != -1) {
				if (k == -1) k = 0;
				int cy = (p - k + m) % m;
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(cy));
				j = (j + 1) % key.length();
			} else {
				output.append(iInput);
			}
		}

		return output.toString();
	}
	public String encryptVN(String input, String key) {
		StringBuilder output = new StringBuilder();
		int m = Alphabet.VN_ALPHABET_FUL.length();
		int j = 0;

		for (int i = 0; i < input.length(); i++) {
			char iInput = input.charAt(i);
			char jInput = key.charAt(j);
			int p = Alphabet.VN_ALPHABET_FUL.indexOf(iInput);
			int k = Alphabet.VN_ALPHABET_FUL.indexOf(jInput);
			if (p != -1) {
				if (k == -1) k = 0;
				int cy = (p + k) % m;
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(cy));
				j = (j + 1) % key.length();
			} else {
				output.append(iInput);
			}
		}

		return output.toString();
	}
	public String decryptVN(String input, String key) {
		StringBuilder output = new StringBuilder();
		int m = Alphabet.VN_ALPHABET_FUL.length();
		int j = 0;

		for (int i = 0; i < input.length(); i++) {
			char iInput = input.charAt(i);
			char jInput = key.charAt(j);
			int p = Alphabet.VN_ALPHABET_FUL.indexOf(iInput);
			int k = Alphabet.VN_ALPHABET_FUL.indexOf(jInput);
			if (p != -1 ) {
				if (k == -1) k = 0;
				int cy = (p - k + m) % m;
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(cy));
				j = (j + 1) % key.length();
			} else {
				output.append(iInput);
			}
		}

		return output.toString();
	}
}
