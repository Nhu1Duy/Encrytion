package view.asymmetric;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AsymmetricConfigPanel extends JPanel {

	private  JTextArea publicKeyArea;
	private  JTextArea privateKeyArea;
	private  JButton genKeyPairBtn;

	public AsymmetricConfigPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(4, 4));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// Panel chứa button tạo key pair
		JPanel genPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		genPanel.setOpaque(false);
		genKeyPairBtn = new JButton("Tạo cặp khóa (2048-bit)");
		genKeyPairBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		genPanel.add(genKeyPairBtn);

		// Public Key Area
		JPanel pubPanel = new JPanel(new BorderLayout(0, 4));
		pubPanel.setBorder(new TitledBorder("Public Key (Công khai)"));
		publicKeyArea = new JTextArea(5, 50);
		publicKeyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		publicKeyArea.setLineWrap(true);
		publicKeyArea.setWrapStyleWord(true);
		JScrollPane pubScroll = new JScrollPane(publicKeyArea);
		pubPanel.add(pubScroll, BorderLayout.CENTER);

		// Private Key Area
		JPanel privPanel = new JPanel(new BorderLayout(0, 4));
		privPanel.setBorder(new TitledBorder("Private Key (Bí mật)"));
		privateKeyArea = new JTextArea(5, 50);
		privateKeyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		privateKeyArea.setLineWrap(true);
		privateKeyArea.setWrapStyleWord(true);
		JScrollPane privScroll = new JScrollPane(privateKeyArea);
		privPanel.add(privScroll, BorderLayout.CENTER);

		// Split pane cho public và private
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pubPanel, privPanel);
		splitPane.setDividerLocation(150);
		splitPane.setResizeWeight(0.5);

		// Assemble
		add(genPanel, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
	}

	public JButton getGenKeyPairBtn() {
		return genKeyPairBtn;
	}

	public String getPublicKeyText() {
		return publicKeyArea.getText().trim();
	}

	public void setPublicKeyText(String text) {
		publicKeyArea.setText(text);
	}

	public String getPrivateKeyText() {
		return privateKeyArea.getText().trim();
	}

	public void setPrivateKeyText(String text) {
		privateKeyArea.setText(text);
	}

	public void clear() {
		publicKeyArea.setText("");
		privateKeyArea.setText("");
	}
}