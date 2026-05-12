package controller;

import Tool.Alphabet;
import model.clasic.*;
import model.mordern.symmetric.*;
import view.MainFrame;
import view.symmetric.SymmetricPanel;

import javax.swing.*;

public class AppContext {

	public static final String MODE_CLASSIC = "Classic";
	public static final String MODE_SYMMETRIC = "Symmetric";
	public static final String LANG_VN = "VN";
	public static final String LANG_EN = "EN";

	public final MainFrame view;

	public String currentMode = MODE_CLASSIC;
	public String currentLanguage = LANG_VN;
	public String classicMethod = "Caesar";

	// ── Classic models ────────────────────────────────────────────────────────
	public final CaesarCipher caesarCipher = new CaesarCipher();
	public final SubstitutionCipher substitutionCipher = new SubstitutionCipher();
	public final VigenereCipher vigenereCipher = new VigenereCipher();
	public final AffineCipher affineCipher = new AffineCipher();
	public final HillCipher hillCipher = new HillCipher();
	public final PermutationCipher permutationCipher = new PermutationCipher();

	public int[][] hillKeyMatrix = null;
	public int hillOriginalLen = -1;

	// ── Symmetric models ──────────────────────────────────────────────────────
	public final AES aesModel = new AES();
	public final DES desModel = new DES();
	public final Blowfish blowfishModel = new Blowfish();
	public final RC4 rc4Model = new RC4();

	public AppContext(MainFrame view) {
		this.view = view;
	}

	// ── State helpers ─────────────────────────────────────────────────────────
	public boolean isVN() {
		return LANG_VN.equals(currentLanguage);
	}

	public boolean isClassicMode() {
		return MODE_CLASSIC.equals(currentMode);
	}

	public boolean isSymmetricMode() {
		return MODE_SYMMETRIC.equals(currentMode);
	}

	public String currentAlphabet() {
		return isVN() ? Alphabet.VN_ALPHABET_FUL : Alphabet.EN_ALPHABET_FUL;
	}

	public int alphabetSize() {
		return (int) currentAlphabet().codePoints().count();
	}

	// ── Symmetric helpers ─────────────────────────────────────────────────────

	public SymmetricCipher currentSymModel() {
	    String algo = view.symmetricPanel.getCurrentAlgo();
	    switch (algo) {
	        case SymmetricPanel.ALGO_DES:
	            return desModel;
	        case SymmetricPanel.ALGO_BLOWFISH:
	            return blowfishModel;
	        case SymmetricPanel.ALGO_RC4:
	            return rc4Model;
	        default:
	            return aesModel;
	    }
	}

	public String currentSymAlgoName() {
	    String algo = view.symmetricPanel.getCurrentAlgo();
	    switch (algo) {
	        case SymmetricPanel.ALGO_DES:
	            return "DES";
	        case SymmetricPanel.ALGO_BLOWFISH:
	            return "Blowfish";
	        case SymmetricPanel.ALGO_RC4:
	            return "RC4";
	        default:
	            return "AES";
	    }
	}

	// ── Dialog helpers ────────────────────────────────────────────────────────
	public void showError(String msg) {
		JOptionPane.showMessageDialog(view.frame, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	public void showInfo(String msg) {
		JOptionPane.showMessageDialog(view.frame, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}
}