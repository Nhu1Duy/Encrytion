package model.clasic;

import Tool.Alphabet;
import java.util.Random;

public class AffineCypher {
	public int[] genKey(int n) {
        Random rand = new Random();
        int a;
        do {
            a = rand.nextInt(n - 1) + 1;
        } while (gcd(a, n) != 1);

        int b = rand.nextInt(n);
        
        return new int[]{a, b};
    }
	private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }

	public int reverseEN(int a) {
		for (int i = 1; i < Alphabet.EN_ALPHABET_FUL.length(); i++) {
			if ((a * i) % Alphabet.EN_ALPHABET_FUL.length() == 1) {
				return i;
			}
		}
		return -1;
	}

	public int reverseVN(int a) {
		for (int i = 1; i < Alphabet.VN_ALPHABET_FUL.length(); i++) {
			if ((a * i) % Alphabet.VN_ALPHABET_FUL.length() == 1) {
				return i;
			}
		}
		return -1;
	}

	public String encryptEN(String input, int a, int b) {
		StringBuilder output = new StringBuilder();
		for (char c : input.toCharArray()) {
			int index = Alphabet.EN_ALPHABET_FUL.indexOf(c);
			if (index != -1) {
				int result = (a * index + b) % Alphabet.EN_ALPHABET_FUL.length();
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(result));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

///result = (reA * (index - b) % m + m) % m;
	public String decryptEN(String input, int a, int b) {
		int reA = reverseEN(a);
		StringBuilder output = new StringBuilder();
		for (char c : input.toCharArray()) {
			int index = Alphabet.EN_ALPHABET_FUL.indexOf(c);
			if (index != -1) {
				int m = Alphabet.EN_ALPHABET_FUL.length();
				int result = (reA * (index - b) % m + m) % m;
				output.append(Alphabet.EN_ALPHABET_FUL.charAt(result));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String encryptVN(String input, int a, int b) {
		StringBuilder output = new StringBuilder();
		for (char c : input.toCharArray()) {
			int index = Alphabet.VN_ALPHABET_FUL.indexOf(c);
			if (index != -1) {
				int result = (a * index + b) % Alphabet.VN_ALPHABET_FUL.length();
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(result));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	public String decryptVN(String input, int a, int b) {
		int reA = reverseVN(a);
		StringBuilder output = new StringBuilder();
		for (char c : input.toCharArray()) {
			int index = Alphabet.VN_ALPHABET_FUL.indexOf(c);
			if (index != -1) {
				int m = Alphabet.VN_ALPHABET_FUL.length();
				int result = (reA * (index - b) % m + m) % m;
				output.append(Alphabet.VN_ALPHABET_FUL.charAt(result));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

}