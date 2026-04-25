package model.clasic;

import Tool.Alphabet;

public class MainTestClassicCipher {

    // ANSI color codes
    static final String RESET  = "\u001B[0m";
    static final String GREEN  = "\u001B[32m";
    static final String RED    = "\u001B[31m";
    static final String CYAN   = "\u001B[36m";
    static final String BOLD   = "\u001B[1m";

    static int passCount = 0;
    static int failCount = 0;

    // ─────────────────────────────────────────────────────────────
    //  MAIN
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println(BOLD + "\n╔══════════════════════════════════════════════════════════╗");
        System.out.println(      "║        CLASSIC CIPHER – COMPREHENSIVE TEST SUITE         ║");
        System.out.println(      "╚══════════════════════════════════════════════════════════╝" + RESET);

        testCaesar();
        testAffine();
        testVigenere();
        testSubstitution();
        testPermutation();
        testHill();

        System.out.println(BOLD + "\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  RESULT: %s%-4d passed%s │ %s%-4d failed%s                         ║%n",
                GREEN, passCount, RESET + BOLD,
                failCount > 0 ? RED : GREEN, failCount, RESET + BOLD);
        System.out.println(      "╚══════════════════════════════════════════════════════════╝" + RESET + "\n");
    }

    // ─────────────────────────────────────────────────────────────
    //  CAESAR CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testCaesar() {
        printHeader("CAESAR CIPHER");
        CaesarCipher cipher = new CaesarCipher();

        // EN – basic
        String plain = "Hello World";
        String key   = "3";
        String enc   = cipher.encryptEN(plain, key);
        String dec   = cipher.decryptEN(enc, key);
        assertTest("EN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // EN – uppercase & digits preserved in FUL alphabet
        plain = "Abc XYZ 123";
        enc   = cipher.encryptEN(plain, "5");
        dec   = cipher.decryptEN(enc, "5");
        assertTest("EN full alphabet (upper + digits)", plain, dec);
        printDetail(plain, enc, dec, "5");

        // EN – key = 0 (identity)
        plain = "NoChange";
        enc   = cipher.encryptEN(plain, "0");
        assertTest("EN key=0 identity", plain, enc);

        // EN – wrap-around
        plain = "xyz";
        enc   = cipher.encryptEN(plain, "3");
        dec   = cipher.decryptEN(enc, "3");
        assertTest("EN wrap-around", plain, dec);
        printDetail(plain, enc, dec, "3");

        // EN – genKey roundtrip
        int genK = cipher.genKey(25);
        plain = "Testing GenKey";
        enc   = cipher.encryptEN(plain, String.valueOf(genK));
        dec   = cipher.decryptEN(enc, String.valueOf(genK));
        assertTest("EN genKey roundtrip (k=" + genK + ")", plain, dec);

        // VN – basic
        plain = "Chao the gioi";
        key   = "5";
        enc   = cipher.encryptVN(plain, key);
        dec   = cipher.decryptVN(enc, key);
        assertTest("VN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // VN – digits
        plain = "Nam 2025";
        enc   = cipher.encryptVN(plain, "10");
        dec   = cipher.decryptVN(enc, "10");
        assertTest("VN with digits", plain, dec);

        // EN – non-alpha chars preserved
        plain = "Hello, World! 42";
        enc   = cipher.encryptEN(plain, "7");
        dec   = cipher.decryptEN(enc, "7");
        assertTest("EN non-alpha chars preserved", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  AFFINE CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testAffine() {
        printHeader("AFFINE CIPHER");
        AffineCipher cipher = new AffineCipher();

        // EN – standard key
        String plain = "Hello World";
        String key   = "7,3";
        String enc   = cipher.encryptEN(plain, key);
        String dec   = cipher.decryptEN(enc, key);
        assertTest("EN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // EN – a=1 (pure shift like Caesar)
        plain = "Affine Test";
        key   = "1,10";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN a=1 (pure shift)", plain, dec);

        // EN – uppercase + digits
        plain = "ABCabc123";
        key   = "3,7";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN full alphabet (upper + digits)", plain, dec);
        printDetail(plain, enc, dec, key);

        // EN – non-alpha preserved
        plain = "Test, 99!";
        enc   = cipher.encryptEN(plain, "5,2");
        dec   = cipher.decryptEN(enc, "5,2");
        assertTest("EN non-alpha chars preserved", plain, dec);

        // EN – genKey roundtrip
        int[] gk  = cipher.genKey(Alphabet.EN_ALPHABET_FUL.length());
        String gks = gk[0] + "," + gk[1];
        plain = "GenKey Test";
        enc   = cipher.encryptEN(plain, gks);
        dec   = cipher.decryptEN(enc, gks);
        assertTest("EN genKey roundtrip (a=" + gk[0] + ",b=" + gk[1] + ")", plain, dec);

        // VN – roundtrip
        plain = "Xin chao Viet Nam";
        key   = "3,5";
        enc   = cipher.encryptVN(plain, key);
        dec   = cipher.decryptVN(enc, key);
        assertTest("VN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // VN – digits
        plain = "Bai 01 va 02";
        key   = "7,11";
        enc   = cipher.encryptVN(plain, key);
        dec   = cipher.decryptVN(enc, key);
        assertTest("VN with digits", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  VIGENERE CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testVigenere() {
        printHeader("VIGENERE CIPHER");
        VigenereCipher cipher = new VigenereCipher();

        // EN – classic ATTACK / LEMON
        String plain = "attackatdawn";
        String key   = "lemon";
        String enc   = cipher.encryptEN(plain, key);
        String dec   = cipher.decryptEN(enc, key);
        assertTest("EN classic attackatdawn/lemon", plain, dec);
        printDetail(plain, enc, dec, key);

        // EN – key longer than text
        plain = "hi";
        key   = "secretkey";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN key longer than text", plain, dec);

        // EN – key same length as text (OTP-like)
        plain = "onepad";
        key   = "onetwo";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN key == text length (OTP-like)", plain, dec);

        // EN – uppercase + digits
        plain = "Hello123";
        key   = "Key5";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN uppercase + digits", plain, dec);
        printDetail(plain, enc, dec, key);

        // EN – genKey roundtrip
        String gk = cipher.genKey(Alphabet.EN_ALPHABET_FUL, 6);
        plain     = "Vigenere";
        enc       = cipher.encryptEN(plain, gk);
        dec       = cipher.decryptEN(enc, gk);
        assertTest("EN genKey roundtrip (key=" + gk + ")", plain, dec);

        // EN – non-alpha preserved
        plain = "Hello, World!";
        key   = "secret";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN non-alpha preserved", plain, dec);

        // VN – basic (ASCII-safe keys for VN test)
        plain = "xin chao";
        key   = "khoa";
        enc   = cipher.encryptVN(plain, key);
        dec   = cipher.decryptVN(enc, key);
        assertTest("VN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // VN – genKey roundtrip
        gk  = cipher.genKey(Alphabet.VN_ALPHABET_FUL, 5);
        plain = "Tieng Viet";
        enc   = cipher.encryptVN(plain, gk);
        dec   = cipher.decryptVN(enc, gk);
        assertTest("VN genKey roundtrip", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  SUBSTITUTION CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testSubstitution() {
        printHeader("SUBSTITUTION CIPHER");
        SubstitutionCipher cipher = new SubstitutionCipher();

        // EN – genKey roundtrip
        String enAlpha = Alphabet.EN_ALPHABET_FUL;
        String enKey   = cipher.genKey(enAlpha);
        String plain   = "Hello World 123";
        String enc     = cipher.encryptEN(plain, enKey);
        String dec     = cipher.decryptEN(enc, enKey);
        assertTest("EN genKey roundtrip", plain, dec);
        printDetail(plain, enc, dec, "[random EN permutation]");

        // EN – non-alpha preserved
        plain = "Test, OK! 42";
        enc   = cipher.encryptEN(plain, enKey);
        dec   = cipher.decryptEN(enc, enKey);
        assertTest("EN non-alpha chars preserved", plain, dec);

        // EN – decrypt after encrypt = original
        plain = "CryptoTest";
        enc   = cipher.encryptEN(plain, enKey);
        dec   = cipher.decryptEN(enc, enKey);
        assertTest("EN decrypt after encrypt = original", plain, dec);

        // EN – fixed shift-by-1 key test
        StringBuilder sbKey = new StringBuilder();
        for (int i = 0; i < enAlpha.length(); i++)
            sbKey.append(enAlpha.charAt((i + 1) % enAlpha.length()));
        String fixedKey = sbKey.toString();
        plain = "abc";
        enc   = cipher.encryptEN(plain, fixedKey);
        dec   = cipher.decryptEN(enc, fixedKey);
        assertTest("EN fixed shift-1 key roundtrip", plain, dec);
        printDetail(plain, enc, dec, "[shift+1 key]");

        // VN – genKey roundtrip
        String vnAlpha = Alphabet.VN_ALPHABET_FUL;
        String vnKey   = cipher.genKey(vnAlpha);
        plain = "Xin chao Viet Nam";
        enc   = cipher.encryptVN(plain, vnKey);
        dec   = cipher.decryptVN(enc, vnKey);
        assertTest("VN genKey roundtrip", plain, dec);
        printDetail(plain, enc, dec, "[random VN permutation]");

        // VN – spaces/punctuation preserved
        plain = "Bai tap so 1!";
        enc   = cipher.encryptVN(plain, vnKey);
        dec   = cipher.decryptVN(enc, vnKey);
        assertTest("VN non-alpha chars preserved", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  PERMUTATION (TRANSPOSITION) CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testPermutation() {
        printHeader("PERMUTATION (TRANSPOSITION) CIPHER");
        PermutationCipher cipher = new PermutationCipher();

        // Basic roundtrip
        String plain = "HELLOWORLD";
        String key   = "2 0 3 1";
        String enc   = cipher.encryptEN(plain, key);
        String dec   = cipher.decryptEN(enc, key);
        assertTest("EN basic roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // Text not divisible by cols (padding)
        plain = "HELLO";
        key   = "1 0 2";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN padding roundtrip (len not multiple of key)", plain, dec);
        printDetail(plain, enc, dec, key);

        // VN (cipher is alphabet-agnostic)
        plain = "xin chao";
        key   = "3 1 0 2";
        enc   = cipher.encryptVN(plain, key);
        dec   = cipher.decryptVN(enc, key);
        assertTest("VN encrypt/decrypt roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // genKey roundtrip
        String gk = cipher.genKey(5);
        plain = "SecretMessage";
        enc   = cipher.encryptEN(plain, gk);
        dec   = cipher.decryptEN(enc, gk);
        assertTest("EN genKey roundtrip (key=" + gk + ")", plain, dec);

        // Single column key (identity)
        plain = "ABCDE";
        key   = "0";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN single column (identity) roundtrip", plain, dec);

        // 2-column key
        plain = "abcdefgh";
        key   = "1 0";
        enc   = cipher.encryptEN(plain, key);
        dec   = cipher.decryptEN(enc, key);
        assertTest("EN 2-col key roundtrip", plain, dec);
        printDetail(plain, enc, dec, key);

        // Longer text
        String gk2 = cipher.genKey(6);
        plain = "The quick brown fox";
        enc   = cipher.encryptEN(plain, gk2);
        dec   = cipher.decryptEN(enc, gk2);
        assertTest("EN longer text genKey roundtrip (key=" + gk2 + ")", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  HILL CIPHER
    // ─────────────────────────────────────────────────────────────
    static void testHill() {
        printHeader("HILL CIPHER");
        HillCipher cipher = new HillCipher();

        // EN – 2x2 known matrix
        int[][] key2x2 = {{3, 3}, {2, 5}};
        String  keyStr = cipher.matrixToKey(key2x2);
        String  plain  = "help";
        String  enc    = cipher.encryptEN(plain, keyStr);
        String  keyLen = cipher.matrixToKey(key2x2, plain.length());
        String  dec    = cipher.decryptEN(enc, keyLen);
        assertTest("EN 2x2 known matrix roundtrip", plain, dec);
        printDetail(plain, enc, dec, keyStr);

        // EN – 3x3 generated key
        int[][] key3x3  = cipher.generateKeyEN(3);
        String  key3Str = cipher.matrixToKey(key3x3);
        plain = "SECRETMSG";
        enc   = cipher.encryptEN(plain, key3Str);
        dec   = cipher.decryptEN(enc, cipher.matrixToKey(key3x3, plain.length()));
        assertTest("EN 3x3 generateKeyEN roundtrip", plain, dec);
        printDetail(plain, enc, dec, "[3x3 generated]");

        // EN – 2x2 generated
        int[][] key2gen = cipher.generateKeyEN(2);
        String  keyGen  = cipher.matrixToKey(key2gen);
        plain = "HelloWorld";
        enc   = cipher.encryptEN(plain, keyGen);
        dec   = cipher.decryptEN(enc, cipher.matrixToKey(key2gen, plain.length()));
        assertTest("EN 2x2 generateKeyEN roundtrip", plain, dec);

        // EN – uppercase + digits (use 3x3 so 9 chars divides evenly, no pad)
        plain = "ABCabc123";
        enc   = cipher.encryptEN(plain, cipher.matrixToKey(key3x3));
        dec   = cipher.decryptEN(enc, cipher.matrixToKey(key3x3, countAlpha(plain, Alphabet.EN_ALPHABET_FUL)));
        assertTest("EN upper + digits roundtrip", plain, dec);

        // EN – 4x4 generated key
        int[][] key4x4 = cipher.generateKeyEN(4);
        plain = "FourByFourMatrix";
        enc   = cipher.encryptEN(plain, cipher.matrixToKey(key4x4));
        dec   = cipher.decryptEN(enc, cipher.matrixToKey(key4x4, plain.length()));
        assertTest("EN 4x4 generateKeyEN roundtrip", plain, dec);

        // VN – 2x2 generated key (6 chars all in VN alpha, divisible by 2)
        int[][] keyVN2  = cipher.generateKeyVN(2);
        String  keyVN2S = cipher.matrixToKey(keyVN2);
        plain = "abcdeg";  // a,b,c,d,e,g all exist in VN alpha (f is not in VN), 6 chars div by 2
        int origLen2 = countAlpha(plain, Alphabet.VN_ALPHABET_FUL);
        enc   = cipher.encryptVN(plain, keyVN2S);
        dec   = cipher.decryptVN(enc, cipher.matrixToKey(keyVN2, origLen2));
        assertTest("VN 2x2 generateKeyVN roundtrip", plain, dec);
        printDetail(plain, enc, dec, "[VN 2x2 generated]");

        // VN – 3x3 generated key (9 chars all in VN alpha, divisible by 3)
        // VN alpha does NOT contain: f, j, w, z — so we avoid them
        int[][] keyVN3  = cipher.generateKeyVN(3);
        String  keyVN3S = cipher.matrixToKey(keyVN3);
        plain = "abcdeghik"; // a,b,c,d,e,g,h,i,k — all in VN alpha, 9 chars divisible by 3
        int origLen3 = countAlpha(plain, Alphabet.VN_ALPHABET_FUL);
        enc   = cipher.encryptVN(plain, keyVN3S);
        dec   = cipher.decryptVN(enc, cipher.matrixToKey(keyVN3, origLen3));
        assertTest("VN 3x3 generateKeyVN roundtrip", plain, dec);
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────
    static void assertTest(String name, String expected, String actual) {
        boolean ok = expected.equals(actual);
        if (ok) {
            passCount++;
            System.out.printf("  %s✔ PASS%s  %s%n", GREEN, RESET, name);
        } else {
            failCount++;
            System.out.printf("  %sX FAIL%s  %s%n", RED, RESET, name);
            System.out.printf("       expected: [%s]%n", expected);
            System.out.printf("       actual  : [%s]%n", actual);
        }
    }

    static void printDetail(String plain, String enc, String dec, String key) {
        System.out.printf("         key=%-28s  plain=%-18s  enc=%-22s  dec=%s%n",
                key, plain, enc, dec);
    }

    static void printHeader(String title) {
        System.out.println(CYAN + BOLD + "\n+-- " + title + " " + "-".repeat(Math.max(0, 54 - title.length())) + RESET);
    }

    static int countAlpha(String text, String alphabet) {
        int[] cps   = alphabet.codePoints().toArray();
        int count   = 0;
        for (int cp : text.codePoints().toArray()) {
            for (int a : cps) { if (a == cp) { count++; break; } }
        }
        return count;
    }

    static String keepAlpha(String text, String alphabet) {
        int[] cps = alphabet.codePoints().toArray();
        StringBuilder sb = new StringBuilder();
        for (int cp : text.codePoints().toArray()) {
            for (int a : cps) { if (a == cp) { sb.appendCodePoint(cp); break; } }
        }
        return sb.toString();
    }
}