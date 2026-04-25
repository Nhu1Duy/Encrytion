package model.clasic;

import java.util.Random;

/**
 * Hill Cipher – mã hoá theo khối dùng phép nhân ma trận mod |alphabet|.
 */
public class HillCipher implements ClassicCipher {

    private static final String EN_ALPHABET = Tool.Alphabet.EN_ALPHABET_FUL;
    private static final String VN_ALPHABET = Tool.Alphabet.VN_ALPHABET_FUL;


    public int[][] generateKeyEN(int n) { return generateKey(n, EN_ALPHABET); }
    public int[][] generateKeyVN(int n) { return generateKey(n, VN_ALPHABET); }

    public int[][] generateKey(int n, String alphabet) {
        int mod = codePointCount(alphabet);
        Random rng = new Random();
        int[][] key = new int[n][n];
        while (true) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    key[i][j] = rng.nextInt(mod);
            int det = ((det(key, n) % mod) + mod) % mod;
            if (det != 0 && gcd(det, mod) == 1) return key;
        }
    }


    public String matrixToKey(int[][] m) {
        return matrixToKey(m, -1);
    }

    public String matrixToKey(int[][] m, int originalLength) {
        int n = m.length;
        StringBuilder sb = new StringBuilder();
        sb.append(n).append(';');
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i > 0 || j > 0) sb.append(',');
                sb.append(m[i][j]);
            }
        if (originalLength >= 0) sb.append('|').append(originalLength);
        return sb.toString();
    }

    @Override
    public String encryptEN(String plainText, String key) {
        return encryptEN(plainText, parseMatrix(key));
    }

    @Override
    public String decryptEN(String cipherText, String key) {
        int len = parseOriginalLength(key, cipherText);
        return decryptEN(cipherText, parseMatrix(key), len);
    }

    @Override
    public String encryptVN(String plainText, String key) {
        return encryptVN(plainText, parseMatrix(key));
    }

    @Override
    public String decryptVN(String cipherText, String key) {
        int len = parseOriginalLength(key, cipherText);
        return decryptVN(cipherText, parseMatrix(key), len);
    }


    public String encryptEN(String plainText, int[][] key) { return encrypt(plainText, key, EN_ALPHABET); }
    public String encryptVN(String plainText, int[][] key) { return encrypt(plainText, key, VN_ALPHABET); }

    public String decryptEN(String cipherText, int[][] key, int originalLength) {
        return decrypt(cipherText, key, EN_ALPHABET, originalLength);
    }
    public String decryptVN(String cipherText, int[][] key, int originalLength) {
        return decrypt(cipherText, key, VN_ALPHABET, originalLength);
    }

    public String encrypt(String plainText, int[][] key, String alphabet) {
        validateKey(key, alphabet);
        return process(plainText, key, alphabet);
    }

    public String decrypt(String cipherText, int[][] key, String alphabet, int originalLength) {
        validateKey(key, alphabet);
        String raw = process(cipherText, invertMatrix(key, alphabet), alphabet);
        return trimToLength(raw, originalLength);
    }



    private String process(String text, int[][] matrix, String alphabet) {
        int     n          = matrix.length;
        int     mod        = codePointCount(alphabet);
        String[] textCps   = codePoints(text);
        String[] alphaCps  = codePoints(alphabet);

        int[]   posMap  = new int[textCps.length];
        int[]   indices = new int[textCps.length];
        int     count   = 0;

        for (int i = 0; i < textCps.length; i++) {
            int pos = indexOf(alphaCps, textCps[i]);
            if (pos >= 0) {
                indices[count] = pos;
                posMap[count]  = i;
                count++;
            }
        }

        int padded = count + (n - count % n) % n;
        int[] padded_indices = new int[padded];
        System.arraycopy(indices, 0, padded_indices, 0, count);
        int padIdx = indexOf(alphaCps, "X");
        if (padIdx < 0) padIdx = 0;
        for (int i = count; i < padded; i++) padded_indices[i] = padIdx;

        int[] encIdx = new int[padded];
        int[] block  = new int[n];
        for (int i = 0; i < padded; i += n) {
            System.arraycopy(padded_indices, i, block, 0, n);
            int[] result = mulMV(matrix, block, mod);
            System.arraycopy(result, 0, encIdx, i, n);
        }

        String[] output = textCps.clone();
        for (int i = 0; i < count; i++)
            output[posMap[i]] = alphaCps[encIdx[i]];

        StringBuilder sb = new StringBuilder();
        for (String s : output) sb.append(s);
        return sb.toString();
    }

    private int[] mulMV(int[][] m, int[] v, int mod) {
        int n = m.length;
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            long s = 0;
            for (int j = 0; j < n; j++) s += (long) m[i][j] * v[j];
            r[i] = (int) ((s % mod + mod) % mod);
        }
        return r;
    }

    private int[][] invertMatrix(int[][] m, String alphabet) {
        int n   = m.length;
        int mod = codePointCount(alphabet);
        int d   = ((det(m, n) % mod) + mod) % mod;
        int dInv = modInverse(d, mod);

        int[][] adj = adjugate(m, n, mod);
        int[][] inv = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                inv[i][j] = (int) (((long) dInv * adj[i][j] % mod + mod) % mod);
        return inv;
    }

    private int[][] adjugate(int[][] m, int n, int mod) {
        int[][] cof = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                cof[i][j] = ((sign * det(minor(m, i, j, n), n - 1) % mod) + mod) % mod;
            }
        int[][] adj = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adj[i][j] = cof[j][i];
        return adj;
    }

    private int[][] minor(int[][] m, int row, int col, int n) {
        int[][] mn = new int[n - 1][n - 1];
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                mn[r][c++] = m[i][j];
            }
            r++;
        }
        return mn;
    }

    private int det(int[][] m, int n) {
        if (n == 1) return m[0][0];
        if (n == 2) return m[0][0] * m[1][1] - m[0][1] * m[1][0];
        int d = 0;
        for (int col = 0; col < n; col++) {
            int sign = (col % 2 == 0) ? 1 : -1;
            d += sign * m[0][col] * det(minor(m, 0, col, n), n - 1);
        }
        return d;
    }


    private String[] codePoints(String s) {
        return s.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toArray(String[]::new);
    }

    private int codePointCount(String s) {
        return (int) s.codePoints().count();
    }

    private int indexOf(String[] arr, String ch) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i].equals(ch)) return i;
        return -1;
    }

    private String trimToLength(String text, int len) {
        return text.codePoints().limit(len)
                   .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                   .toString();
    }

    private int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

    private int modInverse(int a, int mod) {
        a = ((a % mod) + mod) % mod;
        for (int x = 1; x < mod; x++)
            if ((long) a * x % mod == 1) return x;
        throw new IllegalArgumentException("Không có nghịch đảo mod " + mod + " của " + a);
    }

    private void validateKey(int[][] m, String alphabet) {
        int n   = m.length;
        int mod = codePointCount(alphabet);
        int d   = ((det(m, n) % mod) + mod) % mod;
        if (d == 0 || gcd(d, mod) != 1)
            throw new IllegalArgumentException(
                "Ma trận khóa không hợp lệ! det=" + d + " không có nghịch đảo mod " + mod);
    }

    private int[][] parseMatrix(String key) {
        String core = key.contains("|") ? key.substring(0, key.indexOf('|')) : key;
        String[] parts = core.split(";");
        int n = Integer.parseInt(parts[0].trim());
        String[] vals = parts[1].split(",");
        int[][] m = new int[n][n];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Integer.parseInt(vals[idx++].trim());
        return m;
    }

    private int parseOriginalLength(String key, String fallback) {
        int sep = key.indexOf('|');
        if (sep >= 0) return Integer.parseInt(key.substring(sep + 1).trim());
        return codePointCount(fallback);
    }


    public static void main(String[] args) {
    	HillCipher hill = new HillCipher();

        System.out.println("===== TIẾNG ANH =====");
        int[][] keyEN  = hill.generateKeyEN(2);
        String  plainEN = "Hello123";
        int     lenEN   = (int) plainEN.codePoints().count();
        String  encEN   = hill.encryptEN(plainEN, keyEN);
        String  decEN   = hill.decryptEN(encEN, keyEN, lenEN);
        System.out.println("Plain    : " + plainEN);
        System.out.println("Encrypted: " + encEN);
        System.out.println("Decrypted: " + decEN);
        System.out.println("OK? " + plainEN.equals(decEN));

        System.out.println("\n===== TIẾNG VIỆT =====");
        int[][] keyVN  = hill.generateKeyVN(2);
        String  plainVN = "Xin chào Việt Nam 123";
        int     lenVN   = (int) plainVN.codePoints().count();
        String  encVN   = hill.encryptVN(plainVN, keyVN);
        String  decVN   = hill.decryptVN(encVN, keyVN, lenVN);
        System.out.println("Plain    : " + plainVN);
        System.out.println("Encrypted: " + encVN);
        System.out.println("Decrypted: " + decVN);
        System.out.println("OK? " + plainVN.equals(decVN));

        System.out.println("\n===== QUA INTERFACE (String key) =====");
        ClassicCipher c    = new HillCipher();
        String sKey = hill.matrixToKey(keyEN, lenEN);
        System.out.println("Key string: " + sKey);
        String enc2 = c.encryptEN(plainEN, sKey);
        String dec2 = c.decryptEN(enc2, sKey);
        System.out.println("Encrypted: " + enc2);
        System.out.println("Decrypted: " + dec2);
        System.out.println("OK? " + plainEN.equals(dec2));
    }
}
