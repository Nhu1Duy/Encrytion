package controller.asymmetric;

import java.io.File;
import javax.swing.JOptionPane;

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

	private void browseInput() {
		if (!ctx.isAsymmetricMode())
			return;
		File file = FileManager.chooseOpenFile(ctx.view.frame, "All files", new String[] {});
		if (file == null)
			return;

		ctx.view.sidePanel.getInputPathField().setText(file.getAbsolutePath());
		String outputPath = FileManager.buildOutputPath(file.getParent(), file.getAbsolutePath());
		ctx.view.sidePanel.getOutputPathField().setText(outputPath);
	}

	private void browseOutput() {
		if (!ctx.isAsymmetricMode())
			return;
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

		if (!ctx.isAsymmetricMode())
			return;
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

	private void importKey() {

		if (!ctx.isAsymmetricMode())
			return;
		String keyText = FileManager.importKey(ctx.view.frame);
		if (keyText == null || keyText.isBlank())
			return;

		boolean isPublicKey = keyText.contains("PUBLIC");

		if (isPublicKey) {
			ctx.view.asymmetricPanel.setPublicKeyText(keyText.trim());
		} else {
			ctx.view.asymmetricPanel.setPrivateKeyText(keyText.trim());
		}
	}

	private void saveKey() {

		if (!ctx.isAsymmetricMode())
			return;
		boolean encrypt = true;
		String keyText = encrypt ? ctx.view.asymmetricPanel.getPublicKeyText()
				: ctx.view.asymmetricPanel.getPrivateKeyText();

		if (keyText.isBlank()) {
			ctx.showError("Chưa có key để lưu. Vui lòng tạo hoặc nhập key trước.");
			return;
		}
		FileManager.saveKey(ctx.view.frame, keyText);
	}

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