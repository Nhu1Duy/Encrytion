package controller;

import Tool.Alphabet;
import model.clasic.*;
import view.MainFrame;

public class ControllerContext {

    public static final String METHOD_CAESAR       = "Caesar";
    public static final String METHOD_SUBSTITUTION = "Substitution";
    public static final String METHOD_AFFINE        = "Affine";
    public static final String METHOD_VIGENERE      = "Vigenere";
    public static final String METHOD_HILL          = "Hill";
    public static final String METHOD_PERMUTATION   = "Permutation";
    public static final String METHOD_SYMMETRIC = "Symmetric";

    public static final String LANG_VN = "VN";
    public static final String LANG_EN = "EN";

    public final MainFrame view;

    public final CaesarCipher      caesarCipher      = new CaesarCipher();
    public final SubstitutionCipher substitutionCipher = new SubstitutionCipher();
    public final VigenereCipher     vigenereCipher    = new VigenereCipher();
    public final AffineCipher       affineCipher      = new AffineCipher();
    public final HillCipher         hillCipher        = new HillCipher();
    public final PermutationCipher  permutationCipher = new PermutationCipher();

    public String  currentMethod   = METHOD_CAESAR;
    public String  currentLanguage = LANG_VN;
    public int[][] hillKeyMatrix   = null;
    public int     hillOriginalLen = -1;

    public ControllerContext(MainFrame view) {
        this.view = view;
    }

    public boolean isVN()            { return LANG_VN.equals(currentLanguage); }
    public String  currentAlphabet() { return isVN() ? Alphabet.VN_ALPHABET_FUL : Alphabet.EN_ALPHABET_FUL; }
    public int     alphabetSize()    { return (int) currentAlphabet().codePoints().count(); }

    public void showError(String msg) {
        javax.swing.JOptionPane.showMessageDialog(view.frame, msg, "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
