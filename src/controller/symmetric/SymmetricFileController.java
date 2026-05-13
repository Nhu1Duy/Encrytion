package controller.symmetric;

import controller.AppContext;
import util.FileManager;
import view.symmetric.SymmetricConfigPanel;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.crypto.SecretKey;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SymmetricFileController {

	private final AppContext ctx;
	private final SymmetricKeyValidator keyValidator;

	public SymmetricFileController(AppContext ctx, SymmetricKeyValidator keyValidator) {
		this.ctx = ctx;
		this.keyValidator = keyValidator;
	}

	public void bind() {
		ctx.view.itemImportKey.addActionListener(e -> importKey());
		ctx.view.itemSaveKey.addActionListener(e -> saveKey());

		// File section trong SidePanel
		ctx.view.sidePanel.getBrowseInputBtn().addActionListener(e -> browseInput());
		ctx.view.sidePanel.getBrowseOutputBtn().addActionListener(e -> browseOutput());
		ctx.view.sidePanel.getEncryptFileBtn().addActionListener(e -> handleFile(true));
		ctx.view.sidePanel.getDecryptFileBtn().addActionListener(e -> handleFile(false));
	}

	// ── Browse ───────────────────────────────────────────────────────────────

	/**
	 * Duyệt đệ quy toàn bộ component của JFileChooser,
	 * vô hiệu hóa mọi JTextField để người dùng không gõ/paste tên file được.
	 */
	private void disableFileNameField(JFileChooser fc) {
		SwingUtilities.invokeLater(() -> disableTextFieldsRecursive(fc));
	}

	private void disableTextFieldsRecursive(Component c) {
		if (c instanceof JTextField tf) {
			tf.setEditable(false);
			tf.setFocusable(false);
		}
		if (c instanceof Container container) {
			for (Component child : container.getComponents()) {
				disableTextFieldsRecursive(child);
			}
		}
	}

	private void browseInput() {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(true);
		disableFileNameField(fc);

		if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION) return;

		File f = fc.getSelectedFile();
		ctx.view.sidePanel.getInputPathField().setText(f.getAbsolutePath());

		// Tự động tạo tên file đích: cùng thư mục + tên file nguồn + ".enc"
		// (hoặc bỏ ".enc" nếu file nguồn đã có đuôi ".enc")
		String name = f.getName();
		String outputName = name.endsWith(".enc")
				? name.substring(0, name.length() - 4)
				: name + ".enc";
		ctx.view.sidePanel.getOutputPathField()
				.setText(f.getParent() + File.separator + outputName);
	}

	private void browseOutput() {
		// Chỉ cho chọn thư mục đích — tên file giữ nguyên từ file nguồn
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Chọn thư mục lưu file output");
		disableFileNameField(fc);

		// Mở tại thư mục hiện tại của output field (nếu có)
		String currentOut = ctx.view.sidePanel.getOutputPathField().getText().trim();
		if (!currentOut.isEmpty()) {
			File currentFile = new File(currentOut);
			fc.setCurrentDirectory(currentFile.getParentFile() != null
					? currentFile.getParentFile() : currentFile);
		}

		if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION) return;

		// Lấy tên file từ input path, ghép với thư mục mới được chọn
		String inputPath = ctx.view.sidePanel.getInputPathField().getText().trim();
		String outputFileName;
		if (!inputPath.isEmpty()) {
			String inputName = new File(inputPath).getName();
			outputFileName = inputName.endsWith(".enc")
					? inputName.substring(0, inputName.length() - 4)
					: inputName + ".enc";
		} else {
			outputFileName = "output.enc";
		}

		ctx.view.sidePanel.getOutputPathField()
				.setText(fc.getSelectedFile().getAbsolutePath() + File.separator + outputFileName);
	}

	// ── Handle File ──────────────────────────────────────────────────────────

	private void handleFile(boolean encrypt) {
		String src = ctx.view.sidePanel.getInputPathField().getText().trim();
		String dst = ctx.view.sidePanel.getOutputPathField().getText().trim();

		if (src.isEmpty()) { ctx.showError("Vui lòng chọn file Input!"); return; }
		if (dst.isEmpty()) { ctx.showError("Vui lòng chọn đường dẫn Output!"); return; }

		SymmetricConfigPanel panel = ctx.view.symmetricPanel.getCurrentConfigPanel();
		try {
			ctx.currentSymModel().setTransformation(
				panel.getSelectedMode(), panel.getSelectedPadding());

			boolean ok = processFile(src, dst, encrypt);
			if (!ok) return;

			String location = encrypt ? dst : new File(dst).getParent();
			JOptionPane.showMessageDialog(ctx.view.frame,
				(encrypt ? "Mã hóa" : "Giải mã") + " file thành công!\n→ " + location,
				"Thành công", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			ctx.showError((encrypt ? "Lỗi mã hóa" : "Lỗi giải mã") + " file:\n" + ex.getMessage());
		}
	}

	// ── Import Key ───────────────────────────────────────────────────────────

	private void importKey() {
		String keyText = FileManager.importKey(ctx.view.frame);
		if (keyText == null || keyText.isBlank()) return;
		ctx.view.symmetricPanel.getCurrentConfigPanel().setKeyText(keyText.trim());
	}

	// ── Save Key ─────────────────────────────────────────────────────────────

	private void saveKey() {
		String keyText = ctx.view.symmetricPanel.getCurrentConfigPanel().getKeyText();
		if (keyText.isBlank()) {
			ctx.showError("Chưa có key để lưu. Vui lòng tạo hoặc nhập key trước.");
			return;
		}
		FileManager.saveKey(ctx.view.frame, keyText);
	}

	// ── Process File ─────────────────────────────────────────────────────────

	public boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception {
		SecretKey secretKey = keyValidator.resolveCurrentKey();
		if (secretKey == null) return false;
		ctx.currentSymModel().loadKey(secretKey);
		return ctx.currentSymModel().processFile(sourceFile, destFile, encrypt);
	}
}