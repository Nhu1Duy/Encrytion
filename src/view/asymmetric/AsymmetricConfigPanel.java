package view.asymmetric;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import util.FormatButton;

import java.awt.*;

public class AsymmetricConfigPanel extends JPanel {

	private static final int[] KEY_SIZE_LIST = { 1024, 2048, 4096 };
	private static final String[] PADDING_OPTIONS = { "PKCS1Padding", "OAEPWithSHA-1AndMGF1Padding",
			"OAEPWithSHA-256AndMGF1Padding", "OAEPWithSHA-384AndMGF1Padding", "OAEPWithSHA-512AndMGF1Padding",
			"NoPadding" };

	private JComboBox<String> cbKeySize;
	private JComboBox<String> cbPadding;
	private JTextArea txtPublicKey;
	private JTextArea txtPrivateKey;
	private JButton btnGenerate;

	public AsymmetricConfigPanel() {
		setupInterface(); 
	}

	private void setupInterface() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel topPart = new JPanel(new BorderLayout(0, 10));

		JPanel settingsPanel = new JPanel(new GridLayout(2, 2, 5, 5));

		settingsPanel.add(new JLabel("Key Size:"));
		String[] sizes = new String[KEY_SIZE_LIST.length];
		for (int i = 0; i < KEY_SIZE_LIST.length; i++)
			sizes[i] = KEY_SIZE_LIST[i] + " bit";
		cbKeySize = new JComboBox<>(sizes);
		cbKeySize.setSelectedIndex(1);
		settingsPanel.add(cbKeySize);

		settingsPanel.add(new JLabel("Padding Type:"));
		cbPadding = new JComboBox<>(PADDING_OPTIONS);
		cbPadding.setSelectedIndex(0);
		settingsPanel.add(cbPadding);

		btnGenerate = new JButton("⚡ Generate RSA Keys");
		FormatButton.formatButton(btnGenerate, new Color(142, 36, 170));
		btnGenerate.setFocusPainted(false);

		JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonWrapper.add(btnGenerate);

		topPart.add(settingsPanel, BorderLayout.CENTER);
		topPart.add(buttonWrapper, BorderLayout.SOUTH);

		txtPublicKey = makeTextArea();
		JPanel publicBox = makeTitlePanel(txtPublicKey, "Public Key");

		txtPrivateKey = makeTextArea();
		JPanel privateBox = makeTitlePanel(txtPrivateKey, "Private Key");

		JSplitPane splitView = new JSplitPane(JSplitPane.VERTICAL_SPLIT, publicBox, privateBox);
		splitView.setResizeWeight(0.5);
		splitView.setDividerSize(6);

		add(topPart, BorderLayout.NORTH);
		add(splitView, BorderLayout.CENTER);
	}

	private JTextArea makeTextArea() {
		JTextArea area = new JTextArea(7, 45);
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		return area;
	}

	private JPanel makeTitlePanel(JTextArea area, String title) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new TitledBorder(title));
		p.add(new JScrollPane(area), BorderLayout.CENTER);
		return p;
	}


	public int getSelectedKeySize() {
		int index = cbKeySize.getSelectedIndex();
		return (index >= 0) ? KEY_SIZE_LIST[index] : 2048;
	}

	public String getSelectedPadding() {
		return (String) cbPadding.getSelectedItem();
	}

	public String getTransformation() {
		return "RSA/ECB/" + getSelectedPadding();
	}

	public JButton getGenKeyPairBtn() {
		return btnGenerate;
	}

	public String getPublicKeyText() {
		return txtPublicKey.getText().trim();
	}

	public void setPublicKeyText(String text) {
		txtPublicKey.setText(text);
	}

	public String getPrivateKeyText() {
		return txtPrivateKey.getText().trim();
	}

	public void setPrivateKeyText(String text) {
		txtPrivateKey.setText(text);
	}

	public void resetFields() {
		txtPublicKey.setText("");
		txtPrivateKey.setText("");
	}
}