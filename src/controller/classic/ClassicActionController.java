package controller.classic;

import controller.AppContext;
import view.classic.ClassicCipherPanel;

import javax.swing.*;

public class ClassicActionController {

	private final AppContext ctx;
	private final ClassicCipherPanel classicView;
	private final ClassicKeyValidator validator;

	public ClassicActionController(AppContext ctx, ClassicKeyValidator validator) {
		this.ctx = ctx;
		this.classicView = ctx.view.classicPanel;
		this.validator = validator;
	}

	public void bind() {
		bindEncryptDecrypt();
		bindAllGenKeys();
	}

	private void bindEncryptDecrypt() {
		ctx.view.sidePanel.getEncryptBtn().addActionListener(e -> {
			if (ctx.isClassicMode())
				handleAction(true);
		});
		ctx.view.sidePanel.getDecryptBtn().addActionListener(e -> {
			if (ctx.isClassicMode())
				handleAction(false);
		});
	}

	private void handleAction(boolean isEncrypt) {
		String input = ctx.view.ioPanel.getInputArea().getText().trim();
		if (input.isEmpty()) {
			ctx.showError("Vui lòng nhập văn bản!");
			return;
		}

		try {
			String result = handleCipher(input, isEncrypt);
			ctx.view.ioPanel.getOutputArea().setText(result);
		} catch (NumberFormatException ex) {
			ctx.showError("Khóa phải là số hợp lệ!");
		} catch (Exception ex) {
			ctx.showError(ex.getMessage());
		}
	}

	private String handleCipher(String input, boolean enc) throws Exception {
		String method = ctx.classicMethod;
		switch (method) {
		case ClassicCipherPanel.CAESAR:
			return handleCaesar(input, enc);
		case ClassicCipherPanel.SUBSTITUTION:
			return handleSubstitution(input, enc);
		case ClassicCipherPanel.AFFINE:
			return handleAffine(input, enc);
		case ClassicCipherPanel.VIGENERE:
			return handleVigenere(input, enc);
		case ClassicCipherPanel.HILL:
			return handleHill(input, enc);
		case ClassicCipherPanel.PERMUTATION:
			return handlePermutation(input, enc);
		default:
			throw new IllegalStateException("Cipher không xác định: " + method);
		}
	}

	
	private String handleCaesar(String input, boolean enc) throws Exception {
		String key = classicView.getCaesarPanel().getTextKeyField().trim();
		if (key.isEmpty())
			throw new Exception("Vui lòng nhập khóa hoặc Gen Key!");
		return enc ? (ctx.isVN() ? ctx.caesarCipher.encryptVN(input, key) : ctx.caesarCipher.encryptEN(input, key))
				: (ctx.isVN() ? ctx.caesarCipher.decryptVN(input, key) : ctx.caesarCipher.decryptEN(input, key));
	}

	private String handleSubstitution(String input, boolean enc) throws Exception {
		String key = classicView.getSubstitutionPanel().getKeyArea().getText().trim();
		if (key.isEmpty())
			throw new Exception("Khóa không được để trống!");
		validator.validateSubstitutionKey(key);
		return enc
				? (ctx.isVN() ? ctx.substitutionCipher.encryptVN(input, key)
						: ctx.substitutionCipher.encryptEN(input, key))
				: (ctx.isVN() ? ctx.substitutionCipher.decryptVN(input, key)
						: ctx.substitutionCipher.decryptEN(input, key));
	}

	private String handleAffine(String input, boolean enc) throws Exception {
		String aStr = classicView.getAffinePanel().getTextKeyA().trim();
		String bStr = classicView.getAffinePanel().getTextKeyB().trim();
		if (aStr.isEmpty() || bStr.isEmpty())
			throw new Exception("Cả a và b không được để trống!");
		int a = Integer.parseInt(aStr), b = Integer.parseInt(bStr);
		validator.validateAffineKey(a, b);
		String key = a + "," + b;
		return enc ? (ctx.isVN() ? ctx.affineCipher.encryptVN(input, key) : ctx.affineCipher.encryptEN(input, key))
				: (ctx.isVN() ? ctx.affineCipher.decryptVN(input, key) : ctx.affineCipher.decryptEN(input, key));
	}

	private String handleVigenere(String input, boolean enc) throws Exception {
		String key = classicView.getVigenerePanel().getKeyField().getText().trim();
		if (key.isEmpty())
			throw new Exception("Khóa không được để trống!");
		return enc ? (ctx.isVN() ? ctx.vigenereCipher.encryptVN(input, key) : ctx.vigenereCipher.encryptEN(input, key))
				: (ctx.isVN() ? ctx.vigenereCipher.decryptVN(input, key) : ctx.vigenereCipher.decryptEN(input, key));
	}

	private String handleHill(String input, boolean enc) throws Exception {
		if (ctx.hillKeyMatrix == null)
			throw new Exception("Vui lòng nhấn 'Gen Key' để tạo khóa Hill trước!");
		if (enc) {
			String alpha = ctx.currentAlphabet();
			ctx.hillOriginalLen = (int) input.chars().filter(c -> alpha.contains(String.valueOf((char) c))).count();
			return ctx.isVN() ? ctx.hillCipher.encryptVN(input, ctx.hillKeyMatrix)
					: ctx.hillCipher.encryptEN(input, ctx.hillKeyMatrix);
		} else {
			if (ctx.hillOriginalLen < 0)
				throw new Exception("Hãy mã hóa trước rồi mới giải mã (cần biết độ dài gốc)!");
			return ctx.isVN() ? ctx.hillCipher.decryptVN(input, ctx.hillKeyMatrix, ctx.hillOriginalLen)
					: ctx.hillCipher.decryptEN(input, ctx.hillKeyMatrix, ctx.hillOriginalLen);
		}
	}

	private String handlePermutation(String input, boolean enc) throws Exception {
		String key = classicView.getPermutationPanel().getKeyField().getText().trim();
		if (key.isEmpty())
			throw new Exception("Khóa hoán vị không được để trống!");
		return enc
				? (ctx.isVN() ? ctx.permutationCipher.encryptVN(input, key)
						: ctx.permutationCipher.encryptEN(input, key))
				: (ctx.isVN() ? ctx.permutationCipher.decryptVN(input, key)
						: ctx.permutationCipher.decryptEN(input, key));
	}

	private void bindAllGenKeys() {
		bindCaesarGenKey();
		bindSubstitutionGenKey();
		bindAffineGenKey();
		bindVigenereGenKey();
		bindHillGenKey();
		bindPermutationGenKey();
	}

	private void bindCaesarGenKey() {
		classicView.getCaesarPanel().getGenBtn().addActionListener(e -> {
			try {
				String s = classicView.getCaesarPanel().getTextKeyLenField().trim();
				int max = s.isEmpty() ? ctx.alphabetSize() : Integer.parseInt(s);
				classicView.getCaesarPanel().setKeyField(String.valueOf(ctx.caesarCipher.genKey(max)));
			} catch (NumberFormatException ex) {
				ctx.showError("Giới hạn phải là số nguyên dương!");
			}
		});
	}

	private void bindSubstitutionGenKey() {
		classicView.getSubstitutionPanel().getGenBtn().addActionListener(e -> classicView.getSubstitutionPanel()
				.getKeyArea().setText(ctx.substitutionCipher.genKey(ctx.currentAlphabet())));
	}

	private void bindAffineGenKey() {
		classicView.getAffinePanel().getGenBtn().addActionListener(e -> {
			int[] k = ctx.affineCipher.genKey(ctx.alphabetSize());
			classicView.getAffinePanel().getKeyA().setText(String.valueOf(k[0]));
			classicView.getAffinePanel().getKeyB().setText(String.valueOf(k[1]));
		});
	}

	private void bindVigenereGenKey() {
		classicView.getVigenerePanel().getGenBtn().addActionListener(e -> {
			try {
				String s = classicView.getVigenerePanel().getTextKeyLenField().trim();
				int len = s.isEmpty() ? 8 : Integer.parseInt(s);
				classicView.getVigenerePanel().getKeyField()
						.setText(ctx.vigenereCipher.genKey(ctx.currentAlphabet(), len));
			} catch (NumberFormatException ex) {
				ctx.showError("Độ dài khóa phải là số nguyên dương!");
			}
		});
	}

	private void bindHillGenKey() {
		classicView.getHillPanel().getGenBtn().addActionListener(e -> {
			try {
				String s = classicView.getHillPanel().getSizeField().trim();
				int n = s.isEmpty() ? 2 : Integer.parseInt(s);
				if (n < 2 || n > 5)
					throw new IllegalArgumentException("Kích thước ma trận phải từ 2 đến 5!");
				ctx.hillKeyMatrix = ctx.isVN() ? ctx.hillCipher.generateKeyVN(n) : ctx.hillCipher.generateKeyEN(n);
				ctx.hillOriginalLen = -1;
				classicView.getHillPanel().setKeyDisplay(ctx.hillCipher.matrixToKey(ctx.hillKeyMatrix));
			} catch (NumberFormatException ex) {
				ctx.showError("Kích thước ma trận phải là số nguyên!");
			} catch (IllegalArgumentException ex) {
				ctx.showError(ex.getMessage());
			}
		});
	}

	private void bindPermutationGenKey() {
		classicView.getPermutationPanel().getGenBtn().addActionListener(e -> {
			try {
				String s = classicView.getPermutationPanel().getLenField().getText().trim();
				int len = s.isEmpty() ? 5 : Integer.parseInt(s);
				classicView.getPermutationPanel().getKeyField().setText(ctx.permutationCipher.genKey(len));
			} catch (NumberFormatException ex) {
				ctx.showError("Độ dài khóa hoán vị phải là số nguyên dương!");
			}
		});
	}
}
