package controller.symmetric;

import controller.AppContext;
import util.FileManager;
import view.symmetric.SymmetricConfigPanel;
import java.io.File;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;

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
		ctx.view.sidePanel.getBrowseInputBtn().addActionListener(e -> browseInput());
		ctx.view.sidePanel.getBrowseOutputBtn().addActionListener(e -> browseOutput());
		ctx.view.sidePanel.getEncryptFileBtn().addActionListener(e -> handleFile(true));
		ctx.view.sidePanel.getDecryptFileBtn().addActionListener(e -> handleFile(false));
	}

	private void browseInput() {
		File file = FileManager.chooseOpenFile(ctx.view.frame, "All files", new String[] {});
		if (file == null)
			return;

		ctx.view.sidePanel.getInputPathField().setText(file.getAbsolutePath());

		String outputPath = FileManager.buildOutputPath(file.getParent(), file.getAbsolutePath());
		ctx.view.sidePanel.getOutputPathField().setText(outputPath);
	}

	private void browseOutput() {
		String currentOut = ctx.view.sidePanel.getOutputPathField().getText().trim();
		File initialDir = currentOut.isEmpty() ? null : new File(currentOut).getParentFile();

		File selectedDir = FileManager.chooseDirectory(ctx.view.frame, initialDir);
		if (selectedDir == null)
			return;

		String inputPath = ctx.view.sidePanel.getInputPathField().getText().trim();
		String outputPath = FileManager.buildOutputPath(selectedDir.getAbsolutePath(), inputPath);
		ctx.view.sidePanel.getOutputPathField().setText(outputPath);
	}

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

		SymmetricConfigPanel panel = ctx.view.symmetricPanel.getCurrentConfigPanel();

		try {
			ctx.currentSymModel().setTransformation(panel.getSelectedMode(), panel.getSelectedPadding());
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


	private void importKey() {
		String keyText = FileManager.importKey(ctx.view.frame);
		if (keyText == null || keyText.isBlank())
			return;

		ctx.view.symmetricPanel.getCurrentConfigPanel().setKeyText(keyText.trim());
	}


	private void saveKey() {
		String keyText = ctx.view.symmetricPanel.getCurrentConfigPanel().getKeyText();
		if (keyText.isBlank()) {
			ctx.showError("Chưa có key để lưu. Vui lòng tạo hoặc nhập key trước.");
			return;
		}
		FileManager.saveKey(ctx.view.frame, keyText);
	}

	public boolean processFile(String sourceFile, String destFile, boolean encrypt) throws Exception {
		SecretKey secretKey = keyValidator.resolveCurrentKey();
		if (secretKey == null)
			return false;

		ctx.currentSymModel().loadKey(secretKey);
		return ctx.currentSymModel().processFile(sourceFile, destFile, encrypt);
	}
}