package model.clasic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

public class PermutationCipher {
	private static final char PADDING_CHAR = 'X';

	public String genKey(int length) {
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

	public String encrypt(String input, String key) {
	    if (key == null || key.isEmpty()) return input;

	    String cleanKey = key.replace(" ", "");
	    int columns = cleanKey.length(); 
	    int rows = (int) Math.ceil((double) input.length() / columns);
	    
	    char[][] grid = new char[rows][columns];
	    int charIdx = 0;

	    for (int i = 0; i < rows; i++) {
	        for (int j = 0; j < columns; j++) {
	            if (charIdx < input.length()) {
	                grid[i][j] = input.charAt(charIdx++);
	            } else {
	                grid[i][j] = PADDING_CHAR;
	            }
	        }
	    }

	    Integer[] keyOrder = getKeyOrder(cleanKey); 

	    StringBuilder output = new StringBuilder();
	    for (int colIndex : keyOrder) {
	        for (int row = 0; row < rows; row++) {
	            output.append(grid[row][colIndex]);
	        }
	    }
	    return output.toString();
	}

	public String decrypt(String input, String key) {
	    if (key == null || key.isEmpty()) return input;

	    String cleanKey = key.replace(" ", "");
	    int columns = cleanKey.length();
	    int rows = input.length() / columns;
	    char[][] grid = new char[rows][columns];

	    Integer[] keyOrder = getKeyOrder(cleanKey);
	    int charIdx = 0;

	    for (int colIndex : keyOrder) {
	        for (int row = 0; row < rows; row++) {
	            grid[row][colIndex] = input.charAt(charIdx++);
	        }
	    }

	    StringBuilder output = new StringBuilder();
	    for (int i = 0; i < rows; i++) {
	        for (int j = 0; j < columns; j++) {
	            output.append(grid[i][j]);
	        }
	    }

	    String result = output.toString();
	    while (result.endsWith(String.valueOf(PADDING_CHAR))) {
	        result = result.substring(0, result.length() - 1);
	    }
	    return result;
	}

	private Integer[] getKeyOrder(String cleanKey) {
	    int n = cleanKey.length();
	    Integer[] indices = new Integer[n];
	    for (int i = 0; i < n; i++) indices[i] = i;

	    Arrays.sort(indices, (a, b) -> {
	        int charComp = Character.compare(cleanKey.charAt(a), cleanKey.charAt(b));
	        if (charComp != 0) return charComp;
	        return Integer.compare(a, b);
	    });
	    return indices;
	}


}
