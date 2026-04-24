package model.clasic;

import java.util.Random;

public class HillCipher {

    private static String EN_ALPHABET = Tool.Alphabet.EN_ALPHABET_FUL;
    private static String VN_ALPHABET = Tool.Alphabet.VN_ALPHABET_FUL;

    public int[][] generateKey(int n, String alphabet) {
        int mod = getLength(alphabet);
        Random random = new Random();
        int[][] key = new int[n][n];

        while (true) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    key[i][j] = random.nextInt(mod);

            int det = ((determinant(key, n) % mod) + mod) % mod;
            if (det != 0 && gcd(det, mod) == 1) return key;
        }
    }

    public int[][] generateKeyEN(int n) { return generateKey(n, EN_ALPHABET); }
    public int[][] generateKeyVN(int n) { return generateKey(n, VN_ALPHABET); }


    public String encrypt(String plainText, int[][] keyMatrix, String alphabet) {
        validateKey(keyMatrix, alphabet);
        return process(plainText, keyMatrix, alphabet);
    }

    public String encryptEN(String plainText, int[][] keyMatrix) {
        return encrypt(plainText, keyMatrix, EN_ALPHABET);
    }

    public String encryptVN(String plainText, int[][] keyMatrix) {
        return encrypt(plainText, keyMatrix, VN_ALPHABET);
    }

 
    public String decrypt(String cipherText, int[][] keyMatrix, String alphabet, int originalLength) {
        validateKey(keyMatrix, alphabet);
        int[][] inverseKey = invertMatrix(keyMatrix, alphabet);
        String result = process(cipherText, inverseKey, alphabet);
        return removePadding(result, originalLength);
    }

    public String decryptEN(String cipherText, int[][] keyMatrix, int originalLength) {
        return decrypt(cipherText, keyMatrix, EN_ALPHABET, originalLength);
    }

    public String decryptVN(String cipherText, int[][] keyMatrix, int originalLength) {
        return decrypt(cipherText, keyMatrix, VN_ALPHABET, originalLength);
    }

 
    private String process(String text, int[][] matrix, String alphabet) {
        int n = matrix.length;
        int mod = getLength(alphabet);
        String[] textChars = splitChars(text);
        String[] alphaChars = splitChars(alphabet);

        StringBuilder inAlpha = new StringBuilder();
        int[] posMap = new int[textChars.length];
        int count = 0;

        for (int i = 0; i < textChars.length; i++) {
            if (indexOf(alphaChars, textChars[i]) >= 0) {
                inAlpha.append(textChars[i]);
                posMap[count++] = i;
            }
        }

        String cleaned = inAlpha.toString();
        String padChar = "X"; 
        while (getLength(cleaned) % n != 0) cleaned += padChar;

        String[] cleanChars = splitChars(cleaned);
        String[] encChars = new String[cleanChars.length];

        for (int i = 0; i < cleanChars.length; i += n) {
            int[] block = new int[n];
            for (int j = 0; j < n; j++)
                block[j] = indexOf(alphaChars, cleanChars[i + j]);

            int[] result = multiplyMatrixVector(matrix, block, mod);

            for (int j = 0; j < n; j++)
                encChars[i + j] = alphaChars[result[j]];
        }

        String[] output = textChars.clone();
        int encIdx = 0;
        for (int i = 0; i < count; i++) {
            output[posMap[i]] = encChars[encIdx++];
        }

        StringBuilder sb = new StringBuilder();
        for (String ch : output) sb.append(ch);
        return sb.toString();
    }

    private String[] splitChars(String s) {
        return s.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toArray(String[]::new);
    }

    private int getLength(String s) {
        return (int) s.codePoints().count();
    }

    private int indexOf(String[] alphaChars, String ch) {
        for (int i = 0; i < alphaChars.length; i++)
            if (alphaChars[i].equals(ch)) return i;
        return -1;
    }

    private int[] multiplyMatrixVector(int[][] matrix, int[] vector, int mod) {
        int n = matrix.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = 0; j < n; j++) sum += (long) matrix[i][j] * vector[j];
            result[i] = (int) ((sum % mod + mod) % mod);
        }
        return result;
    }

    private int[][] invertMatrix(int[][] matrix, String alphabet) {
        int n = matrix.length;
        int mod = getLength(alphabet);
        int det = ((determinant(matrix, n) % mod) + mod) % mod;
        int detInv = modInverse(det, mod);

        int[][] adj = adjugate(matrix, n, mod);
        int[][] inverse = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                inverse[i][j] = (int) (((long) detInv * adj[i][j] % mod + mod) % mod);

        return inverse;
    }

    private int[][] adjugate(int[][] matrix, int n, int mod) {
        int[][] cofactors = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                int cof = sign * determinant(getMinor(matrix, i, j, n), n - 1);
                cofactors[i][j] = ((cof % mod) + mod) % mod;
            }
        int[][] adj = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adj[i][j] = cofactors[j][i];
        return adj;
    }

    private int[][] getMinor(int[][] matrix, int row, int col, int n) {
        int[][] minor = new int[n - 1][n - 1];
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[r][c++] = matrix[i][j];
            }
            r++;
        }
        return minor;
    }

    private int determinant(int[][] matrix, int n) {
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        int det = 0;
        for (int col = 0; col < n; col++) {
            int sign = (col % 2 == 0) ? 1 : -1;
            det += sign * matrix[0][col] * determinant(getMinor(matrix, 0, col, n), n - 1);
        }
        return det;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int modInverse(int a, int mod) {
        a = ((a % mod) + mod) % mod;
        for (int x = 1; x < mod; x++)
            if ((long) a * x % mod == 1) return x;
        throw new IllegalArgumentException("Không tìm được nghịch đảo mod " + mod + " của " + a);
    }

    private void validateKey(int[][] keyMatrix, String alphabet) {
        int n = keyMatrix.length;
        int mod = getLength(alphabet);
        int det = ((determinant(keyMatrix, n) % mod) + mod) % mod;
        if (det == 0 || gcd(det, mod) != 1)
            throw new IllegalArgumentException(
                "Ma trận khóa không hợp lệ! det = " + det + " không có nghịch đảo mod " + mod
            );
    }
    private String removePadding(String text, int originalLength) {
        return text.codePoints()
                   .limit(originalLength)
                   .collect(StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append)
                   .toString();
    }


    public static void main(String[] args) {
        HillCipher hill = new HillCipher();

        // ── Tiếng Anh ──────────────────────────────────────────────
        System.out.println("===== TIẾNG ANH =====");
        int[][] keyEN = hill.generateKeyEN(2);
        printMatrix("Key EN", keyEN);

        String plainEN = "Hello123";
        String encEN   = hill.encryptEN(plainEN, keyEN);
        int    lenEN   = (int) plainEN.codePoints()
                            .filter(cp -> EN_ALPHABET.indexOf(cp) >= 0).count();
        String decEN   = hill.decryptEN(encEN, keyEN, lenEN);

        System.out.println("Plain    : " + plainEN);
        System.out.println("Encrypted: " + encEN);
        System.out.println("Decrypted: " + decEN);
        System.out.println("OK? " + plainEN.equals(decEN));

        // ── Tiếng Việt ─────────────────────────────────────────────
        System.out.println("\n===== TIẾNG VIỆT =====");
        int[][] keyVN = hill.generateKeyVN(2);
        printMatrix("Key VN", keyVN);

        String plainVN = "Xin chào Việt Nam 123";
        String encVN   = hill.encryptVN(plainVN, keyVN);
        int    lenVN   = (int) plainVN.codePoints()
                            .filter(cp -> VN_ALPHABET.indexOf(cp) >= 0).count();
        String decVN   = hill.decryptVN(encVN, keyVN, lenVN);

        System.out.println("Plain    : " + plainVN);
        System.out.println("Encrypted: " + encVN);
        System.out.println("Decrypted: " + decVN);
        System.out.println("OK? " + plainVN.equals(decVN));
    }

    private static void printMatrix(String label, int[][] m) {
        System.out.println(label + ":");
        for (int[] row : m) {
            for (int v : row) System.out.printf("%5d", v);
            System.out.println();
        }
    }
}