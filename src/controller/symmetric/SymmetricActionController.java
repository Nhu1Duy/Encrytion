package controller.symmetric;

import controller.AppContext;
import model.mordern.symmetric.*;
import view.symmetric.SymmetricConfigPanel;
import view.symmetric.SymmetricPanel;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.util.Base64;

public class SymmetricActionController {

	private final AppContext ctx;
	private final SymmetricPanel symView;

	// ── Models ────────────────────────────────────────────────────────────────
	private final AES aesModel = new AES();
	private final DES desModel = new DES();
	private final Blowfish blowfishModel = new Blowfish();
	private final RC4 rc4Model = new RC4();

	public SymmetricActionController(AppContext ctx) {
		this.ctx = ctx;
		this.symView = ctx.view.symmetricPanel;
	}

	public void bind() {
		bindGenKeys();
		bindEncryptDecrypt();
	}

	// ── Gen Key ──────────────────────────────────────────────────────────────

	private void bindGenKeys() {
		bindGenKey(symView.getAesPanel(), aesModel, true);
		bindGenKey(symView.getDesPanel(), desModel, false);
		bindGenKey(symView.getBlowfishPanel(), blowfishModel, true);
		bindGenKey(symView.getRc4Panel(), rc4Model, true);
	}

	/**
	 * @param hasVariableKeySize true nếu model hỗ trợ setKeySize() (AES, Blowfish,
	 *                           RC4). false nếu keySize cố định như DES.
	 */
	private void bindGenKey(SymmetricConfigPanel panel, SymmetricCipher model, boolean hasVariableKeySize) {
		panel.getGenBtn().addActionListener(e -> {
			try {
				if (hasVariableKeySize) {
					int keySize = panel.getSelectedKeySize();
					setKeySizeOnModel(model, keySize);
				}

				SecretKey secretKey = model.genKey();

				String keyB64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
				panel.setKeyText(keyB64);

			} catch (Exception ex) {
				showError("Lỗi sinh key: " + ex.getMessage());
			}
		});
	}

	private void setKeySizeOnModel(SymmetricCipher model, int keySize) {
		if (model instanceof AES m)
			m.setKeySize(keySize);
		else if (model instanceof Blowfish m)
			m.setKeySize(keySize);
		else if (model instanceof RC4 m)
			m.setKeySize(keySize);
	}

	private void bindEncryptDecrypt() {
		ctx.view.sidePanel.getEncryptBtn().addActionListener(e -> {
			if (ctx.isSymmetricMode())
				handleCrypto(true);
		});
		ctx.view.sidePanel.getDecryptBtn().addActionListener(e -> {
			if (ctx.isSymmetricMode())
				handleCrypto(false);
		});
	}

	private void handleCrypto(boolean encrypt) {
		String inputText = ctx.view.ioPanel.getInputArea().getText().trim();
		if (inputText.isEmpty()) {
			showError(encrypt ? "Vui lòng nhập văn bản cần mã hóa!" : "Vui lòng nhập văn bản cần giải mã!");
			return;
		}

		SymmetricConfigPanel configPanel = symView.getCurrentConfigPanel();
		String keyB64 = configPanel.getKeyText();
		if (keyB64.isEmpty()) {
			showError("Vui lòng nhập key hoặc nhấn 'Gen Key' để tạo!");
			return;
		}

		String algo = symView.getCurrentAlgo();
		SymmetricCipher model = getModel(algo);

		try {
			byte[] keyBytes = Base64.getDecoder().decode(keyB64);
			SecretKey secretKey = new SecretKeySpec(keyBytes, getAlgorithmName(algo));
			model.loadKey(secretKey);

			String result = encrypt ? model.encryptBase64(inputText) : model.decryptBase64(inputText);

			ctx.view.ioPanel.getOutputArea().setText(result);
			ctx.view.setStatus((encrypt ? "Mã hóa OK – " : "Giải mã OK – ") + algo);

		} catch (IllegalArgumentException ex) {
			showError("Key không hợp lệ (không phải Base64): " + ex.getMessage());
		} catch (Exception ex) {
			showError((encrypt ? "Lỗi mã hóa: " : "Lỗi giải mã: ") + ex.getMessage());
		}
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private SymmetricCipher getModel(String algo) {
		return switch (algo) {
		case SymmetricPanel.ALGO_DES -> desModel;
		case SymmetricPanel.ALGO_BLOWFISH -> blowfishModel;
		case SymmetricPanel.ALGO_RC4 -> rc4Model;
		default -> aesModel;
		};
	}


	private String getAlgorithmName(String algo) {
		return switch (algo) {
		case SymmetricPanel.ALGO_DES -> "DES";
		case SymmetricPanel.ALGO_BLOWFISH -> "Blowfish";
		case SymmetricPanel.ALGO_RC4 -> "RC4";
		default -> "AES";
		};
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(ctx.view.frame, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}