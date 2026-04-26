package model.clasic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

/**
 * Permutation (Transposition) Cipher – sắp xếp lại vị trí ký tự theo key
 */
public class PermutationCipher implements ClassicCipher{
	private static final char PADDING = 'X';

	/// --- GEN KEY ---
	public static String genKey(int length) {
		if (length <= 0)
			return "";
		List<Integer> numbers = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers);

		StringBuilder sb = new StringBuilder();
		for (int num : numbers) {
			sb.append(num).append(" ");
		}
		return sb.toString().trim();
	}

	/// --- Implement ---
	@Override
	public String encryptEN(String plainText, String key) {
		return encrypt(plainText, key);
	}

	@Override
	public String decryptEN(String cipherText, String key) {
		return decrypt(cipherText, key);
	}

	@Override
	public String encryptVN(String plainText, String key) {
		return encrypt(plainText, key);
	}

	@Override
	public String decryptVN(String cipherText, String key) {
		return decrypt(cipherText, key);
	}

	/// --- Handle ---
	private String encrypt(String input, String key) {
		if (key == null || key.isEmpty())
			return input;
		Integer[] order = parseKey(key);
		int cols = order.length;
		int rows = (int) Math.ceil((double) input.length() / cols);

		char[][] grid = new char[rows][cols];
		int idx = 0;
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				grid[r][c] = idx < input.length() ? input.charAt(idx++) : PADDING;

		StringBuilder sb = new StringBuilder(input.length());
		for (int col : order)
			for (int r = 0; r < rows; r++)
				sb.append(grid[r][col]);
		return sb.toString();
	}

	private String decrypt(String input, String key) {
		if (key == null || key.isEmpty())
			return input;
		Integer[] order = parseKey(key);
		int cols = order.length;
		int rows = input.length() / cols;

		char[][] grid = new char[rows][cols];
		int idx = 0;
		for (int col : order)
			for (int r = 0; r < rows; r++)
				grid[r][col] = input.charAt(idx++);

		StringBuilder sb = new StringBuilder(rows * cols);
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				sb.append(grid[r][c]);

		String result = sb.toString();
		int end = result.length();
		while (end > 0 && result.charAt(end - 1) == PADDING)
			end--;
		return result.substring(0, end);
	}

	private Integer[] parseKey(String key) {
		String[] parts = key.trim().split("\\s+");
		Integer[] order = new Integer[parts.length];
		for (int i = 0; i < parts.length; i++)
			order[i] = Integer.parseInt(parts[i]);
		return order;
	}

}
