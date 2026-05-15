package model.clasic;
import Tool.Alphabet;

public class CaesarCipher implements ClassicCipher{
	
	public static int genKey(int max) {
		return (int) (Math.random() * max) + 1;
	}
	
	@Override
    public String encryptEN(String plainText, String key) {
        return handle(plainText, parseKey(key), Alphabet.EN_ALPHABET_FUL);
    }
 
    @Override
    public String decryptEN(String cipherText, String key) {
        return handle(cipherText, -parseKey(key), Alphabet.EN_ALPHABET_FUL);
    }
 
    @Override
    public String encryptVN(String plainText, String key) {
        return handle(plainText, parseKey(key), Alphabet.VN_ALPHABET_FUL);
    }
 
    @Override
    public String decryptVN(String cipherText, String key) {
        return handle(cipherText, -parseKey(key), Alphabet.VN_ALPHABET_FUL);
    }
	
    private String handle(String text, int k, String alphabet) {
        int n = alphabet.length();
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            int idx = alphabet.indexOf(c);
            if (idx >= 0) {
                sb.append(alphabet.charAt(((idx + k) % n + n) % n));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
 
    private int parseKey(String key) {
        return Integer.parseInt(key.trim());
    }

}
