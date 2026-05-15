package controller.classic;

import controller.AppContext;
import util.FileManager;
import view.classic.ClassicCipherPanel;
import view.shared.KeyPanel;

import javax.swing.*;

public class ClassicFileController {

	private static final String HILL_MATRIX_PREFIX = "matrix=";
	private static final String HILL_ORIG_LEN_PREFIX = "origLen=";

	private final AppContext ctx;
	private final ClassicCipherPanel classicView;

	public ClassicFileController(AppContext ctx) {
		this.ctx = ctx;
		this.classicView = ctx.view.classicPanel;
	}

	public void bind() {
		bindImportInput();
		bindSaveOutput();
		bindImportKey();
		bindSaveKey();
		bindClearAll();
	}

	private void bindImportInput() {
		ctx.view.itemImportInput.addActionListener(e -> {
			String content = FileManager.importText(ctx.view.frame);
			if (content != null)
				ctx.view.ioPanel.getInputArea().setText(content);
		});
	}

	private void bindSaveOutput() {
		ctx.view.itemSaveOutput.addActionListener(e -> {
			String content = ctx.view.ioPanel.getOutputArea().getText();
			if (content.isBlank()) {
				ctx.showError("Chưa có kết quả để lưu");
				return;
			}
			FileManager.saveText(ctx.view.frame, content);
		});
	}

	private void bindImportKey() {
		ctx.view.itemImportKey.addActionListener(e -> {
			if (!ctx.isClassicMode())
				return;
			String raw = FileManager.importKey(ctx.view.frame);
			if (raw == null)
				return;
			importKeyToPanel(raw.trim());
		});
	}

	private void importKeyToPanel(String raw) {
		try {
			if (ClassicCipherPanel.HILL.equals(ctx.classicMethod)) {
				importHillKey(raw);
				return;
			}
			KeyPanel kp = classicView.getKeyPanel(ctx.classicMethod);
			if (kp == null)
				return;
			kp.setKeyText(raw);
			ctx.showInfo("Đã nạp khóa vào: " + ctx.classicMethod + " thành công");
		} catch (Exception ex) {
			ctx.showError("Không thể nạp khóa: " + ex.getMessage());
		}
	}

	private void importHillKey(String raw) throws Exception {
		String matrixStr = null;
		int origLen = -1;
		for (String line : raw.split("\\r?\\n")) {
			line = line.trim();
			if (line.startsWith(HILL_MATRIX_PREFIX)) {
				matrixStr = line.substring(HILL_MATRIX_PREFIX.length()).trim();
			} else if (line.startsWith(HILL_ORIG_LEN_PREFIX)) {
				origLen = Integer.parseInt(line.substring(HILL_ORIG_LEN_PREFIX.length()).trim());
			} else if (!line.isEmpty() && matrixStr == null) {
				matrixStr = line;
			}
		}
		if (matrixStr == null || matrixStr.isBlank())
			throw new Exception("File không chứa dữ liệu ma trận hợp lệ!");

		ctx.hillKeyMatrix = parseHillMatrix(matrixStr);
		ctx.hillOriginalLen = origLen;

		classicView.getHillPanel().setKeyDisplay(matrixStr);

		String msg;

		if (origLen >= 0) {
			msg = "Đã nạp khóa Hill thành công.\nĐộ dài gốc: " + origLen;
		} else {
			msg = "Đã nạp khóa Hill thành công.\nChưa có độ dài gốc để giải mã.";
		}
		JOptionPane.showMessageDialog(ctx.view.frame, msg, "Import Key", JOptionPane.INFORMATION_MESSAGE);
	}

	private int[][] parseHillMatrix(String key) throws Exception {
		try {
			String[] parts = key.split(";", 2);
			int n;
			int[] vals;
			if (parts.length == 2) {
				n = Integer.parseInt(parts[0].trim());
				vals = parseTokens(parts[1].trim().split("[,\\s]+"));
			} else {
				String[] tokens = key.trim().split("[,\\s]+");
				n = (int) Math.round(Math.sqrt(tokens.length));
				if (n * n != tokens.length)
					throw new Exception("Không xác định được kích thước ma trận từ " + tokens.length + " phần tử!");
				vals = parseTokens(tokens);
			}
			if (vals.length != n * n)
				throw new Exception(
						"Số phần tử của ma trận không khớp: cần " + (n * n) + ", hiện có " + vals.length + "!");
			int[][] matrix = new int[n][n];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					matrix[i][j] = vals[i * n + j];
			return matrix;
		} catch (NumberFormatException ex) {
			throw new Exception("Dữ liệu ma trận chứa ký tự không hợp lệ!");
		}
	}

	private int[] parseTokens(String[] tokens) {
		int[] vals = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++)
			vals[i] = Integer.parseInt(tokens[i].trim());
		return vals;
	}

	private void bindSaveKey() {
		ctx.view.itemSaveKey.addActionListener(e -> {
			if (!ctx.isClassicMode())
				return;
			String content = buildKeyContent();
			if (content == null || content.isBlank()) {
				ctx.showError("Chưa có khóa để lưu!");
				return;
			}
			FileManager.saveKey(ctx.view.frame, content);
		});
	}

	private String buildKeyContent() {
		if (ClassicCipherPanel.HILL.equals(ctx.classicMethod)) {
			if (ctx.hillKeyMatrix == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append(HILL_MATRIX_PREFIX).append(ctx.hillCipher.matrixToKey(ctx.hillKeyMatrix));
			if (ctx.hillOriginalLen >= 0)
				sb.append("\n").append(HILL_ORIG_LEN_PREFIX).append(ctx.hillOriginalLen);
			return sb.toString();
		}
		KeyPanel kp = classicView.getKeyPanel(ctx.classicMethod);
		return kp != null ? kp.getKeyText() : null;
	}

	private void bindClearAll() {
		ctx.view.itemClearAll.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(ctx.view.frame,
					"Xóa toàn bộ dữ liệu Input, Output và Key hiện tại?", "Xác nhận", JOptionPane.YES_NO_OPTION);
			if (confirm != JOptionPane.YES_OPTION)
				return;
			ctx.view.ioPanel.getInputArea().setText("");
			ctx.view.ioPanel.getOutputArea().setText("");
			clearCurrentKey();
		});
	}

	private void clearCurrentKey() {
		switch (ctx.classicMethod) {
		case ClassicCipherPanel.CAESAR -> classicView.getCaesarPanel().setKeyField("");
		case ClassicCipherPanel.SUBSTITUTION -> classicView.getSubstitutionPanel().getKeyArea().setText("");
		case ClassicCipherPanel.AFFINE -> {
			classicView.getAffinePanel().getKeyA().setText("");
			classicView.getAffinePanel().getKeyB().setText("");
		}
		case ClassicCipherPanel.VIGENERE -> classicView.getVigenerePanel().getKeyField().setText("");
		case ClassicCipherPanel.HILL -> {
			ctx.hillKeyMatrix = null;
			ctx.hillOriginalLen = -1;
			classicView.getHillPanel().setKeyDisplay("(Nhấn Gen key để tạo khóa)");
		}
		case ClassicCipherPanel.PERMUTATION -> classicView.getPermutationPanel().getKeyField().setText("");
		}
	}
}