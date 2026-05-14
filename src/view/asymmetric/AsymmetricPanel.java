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

		add(tabBar,      BorderLayout.NORTH);
		add(configPanel, BorderLayout.CENTER);
	}

	// ── Delegate getters (mirrors SymmetricPanel.getCurrentConfigPanel() pattern) ──

	public AsymmetricConfigPanel getConfigPanel() { return configPanel; }

	public JButton getGenKeyPairBtn()  { return configPanel.getGenKeyPairBtn(); }

	public int    getSelectedKeySize() { return configPanel.getSelectedKeySize(); }
	public String getSelectedPadding() { return configPanel.getSelectedPadding(); }
	public String getTransformation()  { return configPanel.getTransformation(); }

	public String getPublicKeyText()         { return configPanel.getPublicKeyText(); }
	public void   setPublicKeyText(String t) { configPanel.setPublicKeyText(t); }

	public String getPrivateKeyText()         { return configPanel.getPrivateKeyText(); }
	public void   setPrivateKeyText(String t) { configPanel.setPrivateKeyText(t); }

	public void clear() { configPanel.clear(); }
}
