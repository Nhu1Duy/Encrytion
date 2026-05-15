package controller.classic;

import controller.AppContext;

public class ClassicKeyValidator {

    private final AppContext app;

    public ClassicKeyValidator(AppContext app) {
        this.app = app;
    }

    public void validateSubstitutionKey(String inputKey) throws Exception {
        String alphabet = app.currentAlphabet();
        
        if (inputKey == null || inputKey.length() != alphabet.length()) {
            throw new Exception("Key must be exactly " + alphabet.length() + " characters long.");
        }

        boolean[] used = new boolean[alphabet.length()];
        
        for (int i = 0; i < alphabet.length(); i++) {
            char target = alphabet.charAt(i);
            int count = 0;
            
            for (int j = 0; j < inputKey.length(); j++) {
                if (inputKey.charAt(j) == target) {
                    count++;
                }
            }
            
            if (count == 0) {
                throw new Exception("Key is missing the character: " + target);
            }
            if (count > 1) {
                throw new Exception("Character '" + target + "' is duplicated in the key.");
            }
        }
    }

    public void validateAffineKey(int valA, int valB) throws Exception {
        int m = app.alphabetSize();
        
        if (calculateGCD(valA, m) != 1) {
            throw new Exception("Key 'a' (" + valA + ") must be coprime with alphabet size (" + m + ").");
        }
        
        if (valA <= 0) {
            throw new Exception("Key 'a' must be a positive integer.");
        }
    }

    private int calculateGCD(int num1, int num2) {
        while (num2 != 0) {
            int temp = num1 % num2;
            num1 = num2;
            num2 = temp;
        }
        return Math.abs(num1);
    }
}