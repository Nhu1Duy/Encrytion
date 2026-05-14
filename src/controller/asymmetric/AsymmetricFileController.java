package controller.asymmetric;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.AppContext;
import model.mordern.asymmetric.RSAFileV1;
import util.FileManager;

public class AsymmetricFileController {
	private final AppContext ctx;

	public AsymmetricFileController(AppContext ctx) {
		this.ctx = ctx;
	}

	public void bind() {
		ctx.view.itemImportKey.addActionListener(e -> importKey());
		ctx.view.itemSaveKey.addActionListener(e -> saveKey());
		ctx.view.sidePanel.getBrowseInputBtn().addActionListener(e -> browseInput());
		ctx.view.sidePanel.getBrowseOutputBtn().addActionListener(e -> browseOutput());
		ctx.view.sidePanel.getEncryptFileBtn().addActionListener(e -> handleFile(true));
		ctx.view.sidePanel.getDecryptFileBtn().addActionListener(e -> handleFile(false));
	}

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

		if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION)
			return;

		File f = fc.getSelectedFile();
		ctx.view.sidePanel.getInputPathField().setText(f.getAbsolutePath());

		// Tự động tạo tên file đích: cùng thư mục + tên file nguồn + ".enc"
		// (hoặc bỏ ".enc" nếu file nguồn đã có đuôi ".enc")
		String name = f.getName();
		String outputName = name.endsWith(".enc") ? name.substring(0, name.length() - 4) : name + ".enc";
		ctx.view.sidePanel.getOutputPathField().setText(f.getParent() + File.separator + outputName);
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
			fc.setCurrentDirectory(currentFile.getParentFile() != null ? currentFile.getParentFile() : currentFile);
		}

		if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION)
			return;

		// Lấy tên file từ input path, ghép với thư mục mới được chọn
		String inputPath = ctx.view.sidePanel.getInputPathField().getText().trim();
		String outputFileName;
		if (!inputPath.isEmpty()) {
			String inputName = new File(inputPath).getName();
			outputFileName = inputName.endsWith(".enc") ? inputName.substring(0, inputName.length() - 4)
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

		if (src.isEmpty()) {
			ctx.showError("Vui lòng chọn file Input!");
			return;
		}
		if (dst.isEmpty()) {
			ctx.showError("Vui lòng chọn đường dẫn Output!");
			return;
		}

		try {
			boolean ok = processFile(src, dst, encrypt);
			if (!ok)
				return;

			String location = encrypt ? dst : new File(dst).getParent();
			JOptionPane.showMessageDialog(ctx.view.frame,
					(encrypt ? "Mã hóa" : "Giải mã") + " file thành công!\n→ " + location, "Thành công",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			ctx.showError((encrypt ? "Lỗi mã hóa" : "Lỗi giải mã") + " file:\n" + ex.getMessage());
		}
	}

	// ── Import Key ───────────────────────────────────────────────────────────

	private void importKey() {
		String keyText = FileManager.importKey(ctx.view.frame);
		if (keyText == null || keyText.isBlank())
			return;

		// Xác định là public hay private key dựa trên việc chứa "BEGIN PUBLIC" hay
		// "BEGIN PRIVATE"
		boolean isPublicKey = keyText.contains("PUBLIC");

		if (isPublicKey) {
			ctx.view.asymmetricPanel.setPublicKeyText(keyText.trim());
		} else {
			ctx.view.asymmetricPanel.setPrivateKeyText(keyText.trim());
		}
	}

	// ── Save Key ─────────────────────────────────────────────────────────────

	private void saveKey() {
		// Kiểm tra mode: nếu là encryption thì lưu public key, ngược lại lưu private
		// key
		boolean encrypt = true; // default
		String keyText = encrypt ? ctx.view.asymmetricPanel.getPublicKeyText()
				: ctx.view.asymmetricPanel.getPrivateKeyText();

		if (keyText.isBlank()) {
			ctx.showError("Chưa có key để lưu. Vui lòng tạo hoặc nhập key trước.");
			return;
		}
		FileManager.saveKey(ctx.view.frame, keyText);
	}

	// ── Process File ─────────────────────────────────────────────────────────

	public boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception {
		if (encrypt) {
			String pubKeyText = ctx.view.asymmetricPanel.getPublicKeyText();
			if (pubKeyText.isBlank()) {
				ctx.showError("Chưa có public key. Vui lòng tạo hoặc nhập public key.");
				return false;
			}
			var publicKey = RSAFileV1.readPublicKeyFromText(pubKeyText);
			RSAFileV1.doEncryptRSAWithAES(publicKey, sourceFile, destFile);
		} else {
			String privKeyText = ctx.view.asymmetricPanel.getPrivateKeyText();
			if (privKeyText.isBlank()) {
				ctx.showError("Chưa có private key. Vui lòng nhập private key.");
				return false;
			}
			var privateKey = RSAFileV1.readPrivateKeyFromText(privKeyText);
			RSAFileV1.doDecryptRSAWithAES(privateKey, sourceFile, destFile);
		}
		return true;
	}
}
