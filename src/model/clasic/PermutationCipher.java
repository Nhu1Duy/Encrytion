package model.clasic;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermutationCipher implements ClassicCipher {
	
	private int pi[];
	
	private int pi_inv[];
	
	private static final char PADDING = 'X';
	
	public PermutationCipher() {
	    pi = new int[]{};
	    pi_inv = new int[]{};
	}
	private static boolean verify_permutation_key(int[] pi) {
		for(int i = 0; i < pi.length; i++) {
			if(pi[i] < 1 || pi[i] > pi.length) {
				System.err.println("Khóa hoán vị phải chứa các giá trị từ 1 đến " + pi.length + "!");
				return false;
			}
		}
		int[] pi_sorted = pi.clone();
		Arrays.sort(pi_sorted);
		for(int i = 0; i < pi_sorted.length; i++) {
			if(pi_sorted[i] != i + 1) {
				System.err.println("Khóa hoán vị không được bỏ qua bất kỳ giá trị nào!");
				return false;
			}
		}
		
		return true;
	}
	
	private String perform_encryption(String plaintext) {
		int block_count = (int)Math.ceil((double)plaintext.length() / pi.length);
		String[] plaintext_blocks = new String[block_count];
		
		for(int i = 0, idx = 0; i < plaintext.length(); i += pi.length) {
			plaintext_blocks[idx++] = plaintext.substring(i, Math.min(i + pi.length, plaintext.length()));
		}
		
		String ciphertext = "";
		
		for(int i = 0; i < plaintext_blocks.length; i++) {
			for(int j = 0; j < plaintext_blocks[i].length(); j++) {
				ciphertext += plaintext_blocks[i].charAt(pi[j] - 1);
			}
		}
		
		return ciphertext;
	}
	
	private String perform_decryption(String ciphertext) {
		int block_count = (int)Math.ceil((double)ciphertext.length() / pi_inv.length);
		String[] ciphertext_blocks = new String[block_count];
		
		for(int i = 0, idx = 0; i < ciphertext.length(); i += pi_inv.length) {
			ciphertext_blocks[idx++] = ciphertext.substring(i, Math.min(i + pi_inv.length, ciphertext.length()));
		}
		
		String plaintext = "";
		
		for(int i = 0; i < ciphertext_blocks.length; i++) {
			for(int j = 0; j < ciphertext_blocks[i].length(); j++) {
				plaintext += ciphertext_blocks[i].charAt(pi_inv[j] - 1);
			}
		}
		
		int end = plaintext.length();
		while(end > 0 && plaintext.charAt(end - 1) == PADDING) {
			end--;
		}
		
		return plaintext.substring(0, end);
	}
	
	
	public PermutationCipher(int[] input_pi) {
		pi = input_pi;
		pi_inv = new int[pi.length];
		
		for(int i = 0; i < pi.length; i++)
			pi_inv[pi[i] - 1] = i + 1;
	}
	
	
	public static String genKey(int length) {
		if(length <= 0) {
			return "";
		}
		
		List<Integer> numbers = new ArrayList<>();
		for(int i = 0; i < length; i++) {
			numbers.add(i + 1);
		}
		Collections.shuffle(numbers);
		
		StringBuilder key = new StringBuilder();
		for(int i = 0; i < numbers.size(); i++) {
			if(i > 0) key.append(" ");
			key.append(numbers.get(i));
		}
		
		return key.toString();
	}
	
	private int[] parse_key(String key) {
		String[] parts = key.trim().split("\\s+");
		int[] result = new int[parts.length];
		
		for(int i = 0; i < parts.length; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}
		
		return result;
	}
	
	
	@Override
	public String encryptEN(String plainText, String key) {
		if(key == null || key.isEmpty()) {
			return plainText;
		}
		
		int[] input_pi = parse_key(key);
		if(!verify_permutation_key(input_pi)) {
			return plainText;
		}
		
		PermutationCipher cipher = new PermutationCipher(input_pi);
		
		String padded = plainText;
		while(padded.length() % input_pi.length != 0) {
			padded += PADDING;
		}
		
		return cipher.perform_encryption(padded);
	}
	
	@Override
	public String decryptEN(String cipherText, String key) {
		if(key == null || key.isEmpty()) {
			return cipherText;
		}
		
		int[] input_pi = parse_key(key);
		if(!verify_permutation_key(input_pi)) {
			return cipherText;
		}
		
		PermutationCipher cipher = new PermutationCipher(input_pi);
		return cipher.perform_decryption(cipherText);
	}
	
	@Override
	public String encryptVN(String plainText, String key) {
		if(key == null || key.isEmpty()) {
			return plainText;
		}
		
		int[] input_pi = parse_key(key);
		if(!verify_permutation_key(input_pi)) {
			return plainText;
		}
		
		PermutationCipher cipher = new PermutationCipher(input_pi);
		
		String padded = plainText;
		while(padded.length() % input_pi.length != 0) {
			padded += PADDING;
		}
		
		return cipher.perform_encryption(padded);
	}
	
	@Override
	public String decryptVN(String cipherText, String key) {
		if(key == null || key.isEmpty()) {
			return cipherText;
		}
		
		int[] input_pi = parse_key(key);
		if(!verify_permutation_key(input_pi)) {
			return cipherText;
		}
		
		PermutationCipher cipher = new PermutationCipher(input_pi);
		return cipher.perform_decryption(cipherText);
	}
}