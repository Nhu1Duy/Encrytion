package controller.asymmetric;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import javax.swing.JOptionPane;

import controller.AppContext;
import model.mordern.asymmetric.RSAFileV1;

public class AsymmetricActionController {
	
	private final AppContext ctx;
	
	public AsymmetricActionController(AppContext ctx) {
		this.ctx = ctx;
	}
	
	public void bind() {
		bindGenKeys();
		bindEncryptDecrypt();
	}
	
	private void bindGenKeys() {
		ctx.view.asymmetricPanel.getGenKeyPairBtn().addActionListener(e -> {
			try {
				generateKeyPair();
			} catch (Exception ex) {
				ctx.showError("Lỗi tạo cặp khóa: " + ex.getMessage());
			}
		});
	}
	private void generateKeyPair() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
 
		Base64.Encoder encoder = Base64.getEncoder();
		String publicKeyB64 = encoder.encodeToString(kp.getPublic().getEncoded());
		String privateKeyB64 = encoder.encodeToString(kp.getPrivate().getEncoded());
 
		ctx.view.asymmetricPanel.setPublicKeyText(publicKeyB64);
		ctx.view.asymmetricPanel.setPrivateKeyText(privateKeyB64);
 
		JOptionPane.showMessageDialog(
			ctx.view.frame,
			"Đã tạo cặp khóa RSA 2048-bit thành công!",
			"Tạo khóa",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	

	private void bindEncryptDecrypt() {
		ctx.view.sidePanel.getEncryptBtn().addActionListener(e -> {
			if (!ctx.isSymmetricMode())
				handleCrypto(true);
		});
		ctx.view.sidePanel.getDecryptBtn().addActionListener(e -> {
			if (!ctx.isSymmetricMode())
				handleCrypto(false);
		});
	}
	private void handleCrypto(boolean encrypt) {
		String input = ctx.view.ioPanel.getInputArea().getText().trim();
		
		if (input.isEmpty()) {
			ctx.showError(encrypt ? "Vui lòng nhập text cần mã hóa!" : "Vui lòng nhập text cần giải mã!");
			return;
		}
 
		try {
			String result;
			
			if (encrypt) {
				String pubKeyText = ctx.view.asymmetricPanel.getPublicKeyText();
				if (pubKeyText.isBlank()) {
					ctx.showError("Chưa có public key. Vui lòng tạo hoặc nhập public key.");
					return;
				}
				
				var publicKey = RSAFileV1.readPublicKeyFromText(pubKeyText);
				var cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey);
				byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
				result = Base64.getEncoder().encodeToString(encrypted);
				
			} else {
				String privKeyText = ctx.view.asymmetricPanel.getPrivateKeyText();
				if (privKeyText.isBlank()) {
					ctx.showError("Chưa có private key. Vui lòng nhập private key.");
					return;
				}
				
				var privateKey = RSAFileV1.readPrivateKeyFromText(privKeyText);
				var cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(javax.crypto.Cipher.DECRYPT_MODE, privateKey);
				byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(input));
				result = new String(decrypted, "UTF-8");
			}
			
			ctx.view.ioPanel.getOutputArea().setText(result);
			ctx.view.setStatus((encrypt ? "Mã hóa OK – " : "Giải mã OK – ") + "RSA/ECB/PKCS1Padding");
			
		} catch (Exception ex) {
			ctx.showError((encrypt ? "Lỗi mã hóa: " : "Lỗi giải mã: ") + ex.getMessage());
		}
	}
}
 
