package controller.symmetric;

import controller.AppContext;
import model.mordern.symmetric.*;
import view.symmetric.SymmetricConfigPanel;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SymmetricKeyValidator {

	private final AppContext app;

	public SymmetricKeyValidator(AppContext app) {
		this.app = app;
	}

	public SecretKey resolveCurrentKey() {

		String inputKey = app.view.symmetricPanel.getCurrentConfigPanel().getKeyText();
		if (inputKey == null || inputKey.trim().length() == 0) {
			app.showError("Chưa có khóa! Vui lòng nhập hoặc tạo khóa trước.");
			return null;
		}
		try {

			byte[] rawBytes = Base64.getDecoder().decode(inputKey.trim());
			String algorithm = app.currentSymAlgoName();
			SecretKeySpec secret = new SecretKeySpec(rawBytes, algorithm);
			return secret;
		} catch (Exception error) {
			app.showError("Định dạng khóa không hợp lệ: " + error.getLocalizedMessage());
			return null;
		}
	}

	public boolean validateInput(String data, boolean modeEncrypt) {
		if (data == null || data.trim().equals("")) {
			if (modeEncrypt == true) {
				app.showError("Vui lòng nhập nội dung cần mã hóa!");
			} else {
				app.showError("Vui lòng nhập nội dung cần giải mã!");
			}
			return false;
		}
		return true;
	}

	public String generateKeyBase64(SymmetricConfigPanel pnl, SymmetricCipher model, boolean checkSize) {

		try {
			if (checkSize == true) {
				int bitLength = pnl.getSelectedKeySize();
				setupKeyLength(model, bitLength);
			}
			SecretKey keyResult = model.genKey();
			byte[] encodedKey = keyResult.getEncoded();
			return Base64.getEncoder().encodeToString(encodedKey);
		} catch (Exception e) {
			app.showError("Không thể tạo khóa: " + e.getMessage());
			return null;
		}
	}

	private void setupKeyLength(SymmetricCipher cipher, int size) {

		if (cipher instanceof AES) {
			AES temp = (AES) cipher;
			temp.setKeySize(size);
		}

		else if (cipher instanceof Blowfish) {
			Blowfish temp = (Blowfish) cipher;
			temp.setKeySize(size);
		}

		else if (cipher instanceof RC4) {
			RC4 temp = (RC4) cipher;
			temp.setKeySize(size);
		}

		else if (cipher instanceof Twofish) {
			Twofish temp = (Twofish) cipher;
			temp.setKeySize(size);
		}

		else if (cipher instanceof Serpent) {
			Serpent temp = (Serpent) cipher;
			temp.setKeySize(size);
		}
	}
}