package model.clasic;

import Tool.Alphabet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubstitutionCipher {

	public static String genKeySubstitutionCipher(String alphabet) {
		char[] arrayVNAnpabet = alphabet.toCharArray();
		List<Character> letters = new ArrayList<Character>();
		for (char c : arrayVNAnpabet) {
			letters.add(c);
		}
		Collections.shuffle(letters);
		StringBuilder output = new StringBuilder();
		for (char c : letters) {
			output.append(c);
		}
		return output.toString();
	}

	public String encryptEN(String input, String key) {
		StringBuilder output = new StringBuilder();
		String alphabet = Alphabet.EN_ALPHABET_FUL;
		for (char c : input.toCharArray()) {
			int index = alphabet.indexOf(c);
			if (index != -1) {
				output.append(key.charAt(index));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String decryptEN(String input, String key) {
		StringBuilder output = new StringBuilder();
		String alphabet = Alphabet.EN_ALPHABET_FUL;
		for (char c : input.toCharArray()) {
			int index = key.indexOf(c);
			if (index != -1) {
				output.append(alphabet.charAt(index));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String encryptVN(String input, String key) {
		StringBuilder output = new StringBuilder();
		String alphabet = Alphabet.VN_ALPHABET_FUL;
		for (char c : input.toCharArray()) {
			int index = alphabet.indexOf(c);
			if (index != -1) {
				output.append(key.charAt(index));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String decryptVN(String input, String key) {
		StringBuilder output = new StringBuilder();
		String alphabet = Alphabet.VN_ALPHABET_FUL;
		for (char c : input.toCharArray()) {
			int index = key.indexOf(c);
			if (index != -1) {
				output.append(alphabet.charAt(index));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

}
