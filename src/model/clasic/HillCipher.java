package model.clasic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
            int det = (int) (((detLong(key, n) % mod) + mod) % mod);
            if (det != 0 && gcd(det, mod) == 1) return key;
        }
    }


    public String matrixToKey(int[][] m) { return matrixToKey(m, -1); }

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

    @Override public String encryptEN(String plainText, String key) { return encryptEN(plainText, parseMatrix(key)); }
    @Override public String encryptVN(String plainText, String key) { return encryptVN(plainText, parseMatrix(key)); }

    @Override
    public String decryptEN(String cipherText, String key) {
        return decryptEN(cipherText, parseMatrix(key), parseOriginalLength(key));
    }
    @Override
    public String decryptVN(String cipherText, String key) {
        return decryptVN(cipherText, parseMatrix(key), parseOriginalLength(key));
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
        return process(plainText, key, alphabet, -1);
    }

    public String decrypt(String cipherText, int[][] key, String alphabet, int originalAlphaCount) {
        validateKey(key, alphabet);
        return process(cipherText, invertMatrix(key, alphabet), alphabet, originalAlphaCount);
    }
    
    private String process(String text, int[][] matrix, String alphabet, int origAlphaCount) {
        int      n        = matrix.length;
        int      mod      = codePointCount(alphabet);
        String[] textCps  = codePoints(text);
        String[] alphaCps = codePoints(alphabet);

        int[] posMap  = new int[textCps.length];
        int[] indices = new int[textCps.length];
        int   count   = 0;

        for (int i = 0; i < textCps.length; i++) {
            int pos = indexOf(alphaCps, textCps[i]);
            if (pos >= 0) {
                indices[count] = pos;
                posMap[count]  = i;
                count++;
            }
        }

        int   padded     = count + (n - count % n) % n;
        int[] paddedIdx  = new int[padded];
        System.arraycopy(indices, 0, paddedIdx, 0, count);
        int padChar = indexOf(alphaCps, "X");
        if (padChar < 0) padChar = 0;
        for (int i = count; i < padded; i++) paddedIdx[i] = padChar;

        int[] outIdx = new int[padded];
        int[] block  = new int[n];
        for (int i = 0; i < padded; i += n) {
            System.arraycopy(paddedIdx, i, block, 0, n);
            int[] result = mulVM(block, matrix, mod);
            System.arraycopy(result, 0, outIdx, i, n);
        }
        int limit = (origAlphaCount >= 0 && origAlphaCount <= count) ? origAlphaCount : count;

        String[] output = textCps.clone();
        for (int i = 0; i < limit; i++)
            output[posMap[i]] = alphaCps[outIdx[i]];

        List<String> alphaList = Arrays.asList(alphaCps);
        if (origAlphaCount >= 0 && limit < count) {
            int cutPos = posMap[limit]; 
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cutPos; i++) {
                sb.append(output[i]);
            }
            for (int i = cutPos; i < textCps.length; i++) {
                if (!alphaList.contains(textCps[i]))
                    sb.append(textCps[i]);
            }
            return sb.toString();
        }

        StringBuilder sb = new StringBuilder();
        for (String s : output) sb.append(s);
        return sb.toString();
    }


    /** y = x * M  (row-vector nhân bên trái) */
    private int[] mulVM(int[] v, int[][] m, int mod) {
        int n = m.length;
        int[] r = new int[n];
        for (int j = 0; j < n; j++) {
            long s = 0;
            for (int i = 0; i < n; i++) s += (long) v[i] * m[i][j];
            r[j] = (int) ((s % mod + mod) % mod);
        }
        return r;
    }

    private int[][] invertMatrix(int[][] m, String alphabet) {
        int  n    = m.length;
        int  mod  = codePointCount(alphabet);
        long d    = ((detLong(m, n) % mod) + mod) % mod;
        int  dInv = modInverse((int) d, mod);

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
                int  sign     = ((i + j) % 2 == 0) ? 1 : -1;
                long minorDet = detLong(minor(m, i, j, n), n - 1);
                cof[i][j] = (int) (((sign * minorDet) % mod + mod) % mod);
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

    private long detLong(int[][] m, int n) {
        if (n == 0) return 1;
        if (n == 1) return m[0][0];
        if (n == 2) return (long) m[0][0] * m[1][1] - (long) m[0][1] * m[1][0];
        long d = 0;
        for (int col = 0; col < n; col++) {
            int sign = (col % 2 == 0) ? 1 : -1;
            d += sign * (long) m[0][col] * detLong(minor(m, 0, col, n), n - 1);
        }
        return d;
    }

    private int det(int[][] m, int n) { return (int) detLong(m, n); }

    private int modInverse(int a, int mod) {
        a = ((a % mod) + mod) % mod;
        int m0 = mod, x0 = 0, x1 = 1;
        if (mod == 1) return 0;
        int aa = a, mm = mod;
        while (aa > 1) {
            int q = aa / mm, t = mm;
            mm = aa % mm; aa = t; t = x0;
            x0 = x1 - q * x0; x1 = t;
        }
        return x1 < 0 ? x1 + m0 : x1;
    }

    private int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }


    private String[] codePoints(String s) {
        return s.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toArray(String[]::new);
    }

    private int codePointCount(String s) { return (int) s.codePoints().count(); }

    private int indexOf(String[] arr, String ch) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i].equals(ch)) return i;
        return -1;
    }


    private void validateKey(int[][] m, String alphabet) {
        int  n   = m.length;
        int  mod = codePointCount(alphabet);
        int  d   = (int) (((detLong(m, n) % mod) + mod) % mod);
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

    private int parseOriginalLength(String key) {
        int sep = key.indexOf('|');
        return sep >= 0 ? Integer.parseInt(key.substring(sep + 1).trim()) : -1;
    }
}