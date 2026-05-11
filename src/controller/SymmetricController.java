package controller;

import model.mordern.symmetric.AES;
import model.mordern.symmetric.Blowfish;
import model.mordern.symmetric.DES;
import model.mordern.symmetric.RC4;
import model.mordern.symmetric.SymmetricCipher;
import view.MainFrame;
import view.SymmetricConfigPanel;
import view.SymmetricPanel;

import javax.swing.*;

public class SymmetricController {

	private final ControllerContext ctx;

	private final AES aesModel = new AES();
	private final DES desModel = new DES();
	private final Blowfish blowfishModel = new Blowfish();
	private final RC4 rc4Model = new RC4();

	public SymmetricController(ControllerContext ctx) {
		this.ctx = ctx;
	}

	public void bind() {
		bindGenKeys();
		bindEncryptDecrypt();
	}

	private void bindGenKeys() {
		MainFrame view = ctx.view;
		SymmetricPanel sp = view.getSymmetricPanel();

		bindGenKey(sp.getAesPanel(), aesModel);
		bindGenKey(sp.getDesPanel(), desModel);
		bindGenKey(sp.getBlowfishPanel(), blowfishModel);
		bindGenKey(sp.getRc4Panel(), rc4Model);
	}

	private void bindGenKey(SymmetricConfigPanel panel, SymmetricCipher model) {
		panel.getGenBtn().addActionListener(e -> {
			try {
				int keySize = panel.getSelectedKeySize();
//				model.genKey(keySize);
//				panel.setKeyText(model.getKeyAsBase64());
			} catch (Exception ex) {
				showError("Lỗi sinh key: " + ex.getMessage());
			}
		});
	}

	private void bindEncryptDecrypt() {
		MainFrame view = ctx.view;

		view.getEncryptBtn().addActionListener(e -> {
			if (!isSymmetricMode())
				return;
			handleCrypto(true);
		});

		view.getDecryptBtn().addActionListener(e -> {
			if (!isSymmetricMode())
				return;
			handleCrypto(false);
		});
	}
	private void handleCrypto(boolean encrypt) {
	    MainFrame view = ctx.view;
	    SymmetricPanel sp = view.getSymmetricPanel();

	    String inputText = view.getInputArea().getText().trim();

	    if (inputText.isEmpty()) {
	        if (encrypt) {
	            showError("Vui lòng nhập văn bản cần mã hóa!");
	        } else {
	            showError("Vui lòng nhập văn bản cần giải mã!");
	        }
	        return;
	    }

	    SymmetricConfigPanel configPanel = sp.getCurrentConfigPanel();
	    String keyB64 = configPanel.getKeyText();

	    if (keyB64.isEmpty()) {
	        showError("Vui lòng nhập key hoặc nhấn 'Gen Key' để tạo!");
	        return;
	    }

	    SymmetricCipher model = getModel(sp.getCurrentAlgo());

	    try {
//	        model.loadKeyFromBase64(keyB64);
//
//	        String result;
//
//	        if (encrypt) {
//	            result = model.encryptText(inputText);
//	        } else {
//	            result = model.decryptText(inputText);
//	        }

//	        view.getOutputArea().setText(result);

	        if (encrypt) {
	            view.setLanguageStatus("Mã hóa OK – " + sp.getCurrentAlgo());
	        } else {
	            view.setLanguageStatus("Giải mã OK – " + sp.getCurrentAlgo());
	        }

	    } catch (Exception ex) {
	        if (encrypt) {
	            showError("Lỗi mã hóa: " + ex.getMessage());
	        } else {
	            showError("Lỗi giải mã: " + ex.getMessage());
	        }
	    }
	}

	private boolean isSymmetricMode() {
		return ControllerContext.METHOD_SYMMETRIC.equals(ctx.currentMethod);
	}

	private SymmetricCipher getModel(String algo) {

	    if (algo.equals(SymmetricPanel.ALGO_AES)) {
	        return aesModel;
	    }

	    if (algo.equals(SymmetricPanel.ALGO_DES)) {
	        return desModel;
	    }

	    if (algo.equals(SymmetricPanel.ALGO_BLOWFISH)) {
	        return blowfishModel;
	    }

	    if (algo.equals(SymmetricPanel.ALGO_RC4)) {
	        return rc4Model;
	    }

	    return aesModel;
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}