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
            throw new Exception("Khóa phải có đúng " + alphabet.length() + " ký tự.");
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
                throw new Exception("Khóa bị thiếu ký tự: " + target);
            }
            
            if (count > 1) {
                throw new Exception("Ký tự '" + target + "' bị trùng trong khóa.");
            }
        }
    }

    public void validateAffineKey(int valA, int valB) throws Exception {
        int m = app.alphabetSize();
        
        if (calculateGCD(valA, m) != 1) {
            throw new Exception(
                    "Khóa 'a' (" + valA + 
                    ") phải nguyên tố cùng nhau với kích thước bảng chữ cái (" + m + ")."
            );
        }
        
        if (valA <= 0) {
            throw new Exception("Khóa 'a' phải là số nguyên dương.");
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