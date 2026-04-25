package model.clasic;

import Tool.Alphabet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Substitution Cipher – mỗi ký tự trong alphabet được ánh xạ sang ký tự khác theo key
 * Key là một hoán vị của alphabet, key.length() == EN_ALPHABET_FUL.length()
 */
public class SubstitutionCipher implements ClassicCipher{

	/// --- GEN KEY ---
    public static String genKey(String alphabet) {
        List<Character> chars = new ArrayList<>(alphabet.length());
        for (char c : alphabet.toCharArray()) chars.add(c);
        Collections.shuffle(chars);
        StringBuilder sb = new StringBuilder(chars.size());
        for (char c : chars) sb.append(c);
        return sb.toString();
    }
    
	/// --- Implement ---
    @Override
    public String encryptEN(String plainText, String key) {
        return substitute(plainText, key, Alphabet.EN_ALPHABET_FUL, true);
    }
 
    @Override
    public String decryptEN(String cipherText, String key) {
        return substitute(cipherText, key, Alphabet.EN_ALPHABET_FUL, false);
    }
 
    @Override
    public String encryptVN(String plainText, String key) {
        return substitute(plainText, key, Alphabet.VN_ALPHABET_FUL, true);
    }
 
    @Override
    public String decryptVN(String cipherText, String key) {
        return substitute(cipherText, key, Alphabet.VN_ALPHABET_FUL, false);
    }
    
    /// --- Handle ---
    private String substitute(String text, String key, String alphabet, boolean encrypt) {
        String from;
        String to;
        if (encrypt) {
            from = alphabet;
            to = key;
        } else {
            from = key;
            to = alphabet;
        }
 
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            int idx = from.indexOf(c);
            if (idx >= 0) {
                sb.append(to.charAt(idx));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
