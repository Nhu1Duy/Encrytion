package view.asymmetric;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AsymmetricPanel extends JPanel {

	public static final String ALGO_RSA = "RSA";

	private final AsymmetricConfigPanel configPanel;

	public AsymmetricPanel() {
		configPanel = new AsymmetricConfigPanel();
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 4));
		setBorder(new EmptyBorder(4, 0, 4, 0));

		// Header
		JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		tabBar.setOpaque(false);
		JLabel lblAlgo = new JLabel("RSA (Asymmetric Encryption)");
		lblAlgo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		tabBar.add(lblAlgo);

		add(tabBar, BorderLayout.NORTH);
		add(configPanel, BorderLayout.CENTER);
	}
	public JButton getGenKeyPairBtn() {
	    return configPanel.getGenKeyPairBtn();
	}
	public AsymmetricConfigPanel getConfigPanel() {
		return configPanel;
	}

	public String getPublicKeyText() {
		return configPanel.getPublicKeyText();
	}

	public void setPublicKeyText(String text) {
		configPanel.setPublicKeyText(text);
	}

	public String getPrivateKeyText() {
		return configPanel.getPrivateKeyText();
	}

	public void setPrivateKeyText(String text) {
		configPanel.setPrivateKeyText(text);
	}

	public void clear() {
		configPanel.clear();
	}
}