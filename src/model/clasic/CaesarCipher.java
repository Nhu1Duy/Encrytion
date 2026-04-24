package model.clasic;
import Tool.Alphabet;

public class CaesarCipher {
	public int genKey(int max) {
		return (int) (Math.random() * max) + 1;
	}


	public String encryptEN(String input, int k) {
		StringBuilder output = new StringBuilder();
		int n = Alphabet.EN_ALPHABET_FUL.length();
		int index, newIndex;
		for (char c : input.toCharArray()) {
			if (Character.isLetter(c) || (Character.isDigit(c) && Alphabet.EN_ALPHABET_FUL.indexOf(c) != -1)) {
				index = Alphabet.EN_ALPHABET_FUL.indexOf(c);
				newIndex = (index + k) % n;
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(newIndex));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String decryptEN(String input, int k) {
		StringBuilder output = new StringBuilder();
		int n = Alphabet.EN_ALPHABET_FUL.length();
		int index, newIndex;
		for (char c : input.toCharArray()) {
			if (Character.isLetter(c) || (Character.isDigit(c) && Alphabet.EN_ALPHABET_FUL.indexOf(c) != -1)) {
				index = Alphabet.EN_ALPHABET_FUL.indexOf(c);
				newIndex = (index - k % n + n) % n;
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(newIndex));
			}else { 
	            output.append(c);
	        }

		}
		return output.toString();
	}

	public String encryptVN(String input, int k) {
		StringBuilder output = new StringBuilder();
		int n = Alphabet.VN_ALPHABET_FUL.length();
		int index, newIndex;
		for (char c : input.toCharArray()) {
			if ((Character.isLetter(c) && Alphabet.VN_ALPHABET_FUL.indexOf(c) != -1)
					|| (Character.isDigit(c) && Alphabet.VN_ALPHABET_FUL.indexOf(c) != -1)) {
				index = Alphabet.VN_ALPHABET_FUL.indexOf(c);
				newIndex = (index + k) % n;
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(newIndex));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String decryptVN(String input, int k) {
		StringBuilder output = new StringBuilder();
		int n = Alphabet.VN_ALPHABET_FUL.length();
		int index, newIndex;
		for (char c : input.toCharArray()) {
			if ((Character.isLetter(c) && Alphabet.VN_ALPHABET_FUL.indexOf(c) != -1)
					|| (Character.isDigit(c) && Alphabet.VN_ALPHABET_FUL.indexOf(c) != -1)) {
				index = Alphabet.VN_ALPHABET_FUL.indexOf(c);
				newIndex = (index - k % n + n) % n;
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(newIndex));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

}
