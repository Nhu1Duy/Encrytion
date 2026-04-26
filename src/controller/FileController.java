package controller;

import util.FileManager;
import view.KeyPanel;

import javax.swing.JOptionPane;

public class FileController {

	private final ControllerContext ctx;
	private final HillKeyParser hillKeyParser;

	public FileController(ControllerContext ctx, HillKeyParser hillKeyParser) {
		this.ctx = ctx;
		this.hillKeyParser = hillKeyParser;
	}

	public void bind() {
		bindImportInput();
		bindSaveOutput();
		bindImportKey();
		bindSaveKey();
		bindClearAll();
	}


	private void bindImportInput() {
		ctx.view.getItemImportInput().addActionListener(e -> {
			String content = FileManager.importText(ctx.view.frame);
			if (content != null)
				ctx.view.getInputArea().setText(content);
		});
	}

	private void bindSaveOutput() {
		ctx.view.getItemSaveOutput().addActionListener(e -> {
			String content = ctx.view.getOutputArea().getText();
			if (content.isBlank()) {
				ctx.showError("Chưa có kết quả để lưu!");
				return;
			}
			FileManager.saveText(ctx.view.frame, content);
		});
	}


	private void bindImportKey() {
		ctx.view.getItemImportKey().addActionListener(e -> {
			String raw = FileManager.importKey(ctx.view.frame);
			if (raw == null)
				return;
			importKeyToCurrentPanel(raw.trim());
		});
	}

	private void importKeyToCurrentPanel(String raw) {
		try {
			if (ctx.currentMethod.equals(ControllerContext.METHOD_HILL)) {
				hillKeyParser.importHillKey(raw);
				return;
			}

			KeyPanel kp = ctx.view.getCurrentKeyPanel(ctx.currentMethod);
			if (kp == null)
				return;
			kp.setKeyText(raw);

			JOptionPane.showMessageDialog(ctx.view.frame, "Đã nạp khóa vào " + ctx.currentMethod + " thành công.",
					"Import Key", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			ctx.showError("Không thể nạp khóa: " + ex.getMessage());
		}
	}


	private void bindSaveKey() {
		ctx.view.getItemSaveKey().addActionListener(e -> {
			String keyContent = buildKeyFileContent();
			if (keyContent == null || keyContent.isBlank()) {
				ctx.showError("Chưa có khóa để lưu!\nHãy nhập hoặc Gen Key trước.");
				return;
			}
			FileManager.saveKey(ctx.view.frame, keyContent);
		});
	}

	private String buildKeyFileContent() {
		if (ctx.currentMethod.equals(ControllerContext.METHOD_HILL)) {
			if (ctx.hillKeyMatrix == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append(HillKeyParser.HILL_MATRIX_PREFIX).append(ctx.hillCipher.matrixToKey(ctx.hillKeyMatrix));
			if (ctx.hillOriginalLen >= 0) {
				sb.append("\n").append(HillKeyParser.HILL_ORIG_LEN_PREFIX).append(ctx.hillOriginalLen);
			}
			return sb.toString();
		}

		KeyPanel kp = ctx.view.getCurrentKeyPanel(ctx.currentMethod);
		return (kp != null) ? kp.getKeyText() : null;
	}


	private void bindClearAll() {
		ctx.view.getItemClearAll().addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(ctx.view.frame, "Xóa toàn bộ Input, Output và Key hiện tại?",
					"Xác nhận xóa", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				ctx.view.getInputArea().setText("");
				ctx.view.getOutputArea().setText("");
				clearCurrentPanelKey();
			}
		});
	}

	private void clearCurrentPanelKey() {
		switch (ctx.currentMethod) {
		case ControllerContext.METHOD_CAESAR -> ctx.view.getCaesarPanel().setKeyField("");
		case ControllerContext.METHOD_SUBSTITUTION -> ctx.view.getSubstitutionPanel().getKeyArea().setText("");
		case ControllerContext.METHOD_AFFINE -> {
			ctx.view.getAffinePanel().getKeyA().setText("");
			ctx.view.getAffinePanel().getKeyB().setText("");
		}
		case ControllerContext.METHOD_VIGENERE -> ctx.view.getVigenerePanel().getKeyField().setText("");
		case ControllerContext.METHOD_HILL -> {
			ctx.hillKeyMatrix = null;
			ctx.hillOriginalLen = -1;
			ctx.view.getHillPanel().setKeyDisplay("(Nhấn Gen Key để tạo khóa)");
		}
		case ControllerContext.METHOD_PERMUTATION -> ctx.view.getPermutationPanel().getKeyField().setText("");
		}
	}
}
