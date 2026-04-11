package model;

import Tool.Alphabet;

public class HillCypher {
    // ================= EN =================
    public String encryptEN(String input, int[][] keyMatrix) {
        int matrixSize = keyMatrix.length;
        int length = Alphabet.EN_ALPHABET_FUL.length();
        validateDeterminant(keyMatrix, matrixSize, length); 
        
        StringBuilder output = new StringBuilder();
        int[] inputVector = new int[matrixSize];
        int[] outputVector = new int[matrixSize];
        int index = 0;

        while (index < input.length()) {
            for (int i = 0; i < matrixSize; i++) {
                if (index < input.length()) {
                    char c = input.charAt(index++);
                    int pos = Alphabet.EN_ALPHABET_FUL.indexOf(c);
                    inputVector[i] = (pos != -1) ? pos : Alphabet.EN_ALPHABET_FUL.indexOf('X');
                } else {
                    inputVector[i] = Alphabet.EN_ALPHABET_FUL.indexOf('X');
                }
            }

            for (int i = 0; i < matrixSize; i++) {
                long sum = 0; 
                for (int j = 0; j < matrixSize; j++) {
                    sum += (long) keyMatrix[i][j] * inputVector[j];
                }
                int res = (int) (((sum % length) + length) % length);
                output.append(Alphabet.EN_ALPHABET_FUL.charAt(res));
            }
        }
        return output.toString();
    }

    public String decryptEN(String input, int[][] inverseKeyMatrix) {
        int matrixSize = inverseKeyMatrix.length;
        int length = Alphabet.EN_ALPHABET_FUL.length();
        validateDeterminant(inverseKeyMatrix, matrixSize, length);

        StringBuilder output = new StringBuilder();
        int[] inputVector = new int[matrixSize];
        int[] outputVector = new int[matrixSize];
        int index = 0;

        while (index < input.length()) {
            for (int i = 0; i < matrixSize; i++) {
                if (index < input.length()) {
                    char c = input.charAt(index++);
                    inputVector[i] = Alphabet.EN_ALPHABET_FUL.indexOf(c);
                } else {
                    inputVector[i] = Alphabet.EN_ALPHABET_FUL.indexOf('X');
                }
            }

            for (int i = 0; i < matrixSize; i++) {
                long sum = 0;
                for (int j = 0; j < matrixSize; j++) {
                    sum += (long) inverseKeyMatrix[i][j] * inputVector[j];
                }
                int res = (int) (((sum % length) + length) % length);
                output.append(Alphabet.EN_ALPHABET_FUL.charAt(res));
            }
        }
        return output.toString();
    }


    // ================= HELPERS =================
    private void validateDeterminant(int[][] keyMatrix, int n, int mod) {
        int det = determinant(keyMatrix, n);
        det = ((det % mod) + mod) % mod;
        if (gcd(det, mod) != 1) {
            throw new IllegalArgumentException("Ma trận không có nghịch đảo modulo " + mod);
        }
    }

    private int determinant(int[][] matrix, int n) {
        if (n == 1) return matrix[0][0];
        int det = 0;
        int sign = 1;
        for (int x = 0; x < n; x++) {
            int[][] sub = new int[n - 1][n - 1];
            for (int i = 1; i < n; i++) {
                int subJ = 0;
                for (int j = 0; j < n; j++) {
                    if (j != x) sub[i - 1][subJ++] = matrix[i][j];
                }
            }
            det += sign * matrix[0][x] * determinant(sub, n - 1);
            sign = -sign;
        }
        return det;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}