package controller.asymmetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.crypto.spec.IvParameterSpec;
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
		int keySize = ctx.view.asymmetricPanel.getSelectedKeySize();

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keySize);
		KeyPair kp = kpg.generateKeyPair();

		Base64.Encoder encoder = Base64.getEncoder();
		ctx.view.asymmetricPanel.setPublicKeyText(encoder.encodeToString(kp.getPublic().getEncoded()));
		ctx.view.asymmetricPanel.setPrivateKeyText(encoder.encodeToString(kp.getPrivate().getEncoded()));

		JOptionPane.showMessageDialog(ctx.view.frame, "Đã tạo cặp khóa RSA " + keySize + "-bit thành công!", "Tạo khóa",
				JOptionPane.INFORMATION_MESSAGE);
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
            ctx.showError(encrypt ? "Vui lòng nhập text!" : "Dữ liệu trống!");
            return;
        }

        try {
            String result;
            if (encrypt) {
                String pubKeyText = ctx.view.asymmetricPanel.getPublicKeyText();
                PublicKey publicKey = RSAFileV1.readPublicKeyFromText(pubKeyText);

                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128);
                SecretKey skey = kgen.generateKey();
                byte[] iv = new byte[16];
                new SecureRandom().nextBytes(iv);

                Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
                byte[] encryptedText = aesCipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

                Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] wrappedKey = rsaCipher.doFinal(skey.getEncoded());
                byte[] wrappedIv = rsaCipher.doFinal(iv);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                
                dos.writeInt(wrappedKey.length); 
                dos.write(wrappedKey);          
                dos.writeInt(wrappedIv.length);  
                dos.write(wrappedIv);            
                dos.write(encryptedText);       
                
                dos.flush();
                result = Base64.getEncoder().encodeToString(baos.toByteArray());

            } else {
                String privKeyText = ctx.view.asymmetricPanel.getPrivateKeyText();
                PrivateKey privateKey = RSAFileV1.readPrivateKeyFromText(privKeyText);
                
                byte[] decodedData = Base64.getDecoder().decode(input);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(decodedData));

                byte[] wrappedKey = new byte[dis.readInt()];
                dis.readFully(wrappedKey);
                
                byte[] wrappedIv = new byte[dis.readInt()];
                dis.readFully(wrappedIv);
                
                byte[] encryptedContent = dis.readAllBytes();

                Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] keyBytes = rsaCipher.doFinal(wrappedKey);
                byte[] ivBytes = rsaCipher.doFinal(wrappedIv);

                Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
                
                byte[] plainTextBytes = aesCipher.doFinal(encryptedContent);
                result = new String(plainTextBytes, StandardCharsets.UTF_8);
            }
            ctx.view.ioPanel.getOutputArea().setText(result);
            ctx.view.setStatus((encrypt ? "Mã hóa Hybrid OK" : "Giải mã Hybrid OK"));

        } catch (Exception ex) {
            ctx.showError("Lỗi xử lý: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
