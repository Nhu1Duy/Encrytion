package model.clasic;

import Tool.Alphabet;
import java.util.Random;

public class AffineCipher implements ClassicCipher {

	public int[] genKey(int n) {
		Random rand = new Random();
		int a;
		do {
			a = rand.nextInt(n - 1) + 1;
		} while (gcd(a, n) != 1);

		int b = rand.nextInt(n);

		return new int[] { a, b };
	}

	@Override
	public String encryptEN(String plainText, String key) {
		return process(plainText, parseKey(key), true, Alphabet.EN_ALPHABET_FUL);
	}

	@Override
	public String decryptEN(String cipherText, String key) {
		return process(cipherText, parseKey(key), false, Alphabet.EN_ALPHABET_FUL);
	}

	@Override
	public String encryptVN(String plainText, String key) {
		return process(plainText, parseKey(key), true, Alphabet.VN_ALPHABET_FUL);
	}

	@Override
	public String decryptVN(String cipherText, String key) {
		return process(cipherText, parseKey(key), false, Alphabet.VN_ALPHABET_FUL);
	}

    private String process(String text, int[] ab, boolean encrypt, String alphabet) {
        int m   = alphabet.length();
        int a   = ab[0], b = ab[1];
        int aInv = modInverse(a, m);
 
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            int idx = alphabet.indexOf(c);
            if (idx >= 0) {
                int result;

                if (encrypt) {
                    result = (a * idx + b) % m;
                } else {
                    result = (aInv * (idx - b)) % m;
                    if (result < 0) {
                        result = result + m;
                    }
                }
                sb.append(alphabet.charAt(result));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

	private int[] parseKey(String key) {
		String[] parts = key.split(",");
		String part1 = parts[0].trim();
		String part2 = parts[1].trim();

		int a = Integer.parseInt(part1);
		int b = Integer.parseInt(part2);

		int[] result = new int[2];
		result[0] = a;
		result[1] = b;

		return result;
	}

	private int gcd(int a, int b) {
		if (b == 0) {
			return a;
		} else {
			return gcd(b, a % b);
		}
	}

	private int modInverse(int a, int m) {
		a = a % m;
		if (a < 0) {
			a = a + m;
		}
		for (int x = 1; x < m; x++) {
			if ((a * x) % m == 1) {
				return x;
			}
		}
		throw new IllegalArgumentException("Không có nghịch đảo mod " + m + " của " + a);
	}

}
