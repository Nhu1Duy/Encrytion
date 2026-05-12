package controller.symmetric;

import controller.AppContext;
import util.FileManager;
import view.symmetric.SymmetricConfigPanel;

import java.io.File;

import javax.crypto.SecretKey;
import javax.swing.JFileChooser;
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

		// File section trong SidePanel
		ctx.view.sidePanel.getBrowseInputBtn().addActionListener(e -> browseInput());
		ctx.view.sidePanel.getBrowseOutputBtn().addActionListener(e -> browseOutput());
		ctx.view.sidePanel.getEncryptFileBtn().addActionListener(e -> handleFile(true));
		ctx.view.sidePanel.getDecryptFileBtn().addActionListener(e -> handleFile(false));
	}

	// ── Browse ───────────────────────────────────────────────────────────────

	private void browseInput() {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(true);
		if (fc.showOpenDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION) return;

		File f = fc.getSelectedFile();
		ctx.view.sidePanel.getInputPathField().setText(f.getAbsolutePath());

		String name = f.getName();
		if (name.endsWith(".enc")) {
			ctx.view.sidePanel.getOutputPathField()
				.setText(f.getParent() + File.separator + "output.tmp");
		} else {
			ctx.view.sidePanel.getOutputPathField()
				.setText(f.getAbsolutePath() + ".enc");
		}
	}
	private void browseOutput() {
	    JFileChooser fc = new JFileChooser();
	    fc.setAcceptAllFileFilterUsed(true);
	    fc.setDialogTitle("Chọn vị trí lưu file output");

	    // Gợi ý tên file từ field hiện tại
	    String currentOut = ctx.view.sidePanel.getOutputPathField().getText().trim();
	    if (!currentOut.isEmpty()) {
	        fc.setSelectedFile(new File(currentOut));
	    }

	    if (fc.showSaveDialog(ctx.view.frame) != JFileChooser.APPROVE_OPTION) return;

	    ctx.view.sidePanel.getOutputPathField()
	        .setText(fc.getSelectedFile().getAbsolutePath());
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