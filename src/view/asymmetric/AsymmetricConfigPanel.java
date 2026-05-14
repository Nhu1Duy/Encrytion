package view.asymmetric;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AsymmetricConfigPanel extends JPanel {

	private static final int[]    KEY_SIZES = { 1024, 2048, 4096 };
	private static final String[] PADDINGS  = {
		"PKCS1Padding",
		"OAEPWithSHA-1AndMGF1Padding",
		"OAEPWithSHA-256AndMGF1Padding",
		"OAEPWithSHA-384AndMGF1Padding",
		"OAEPWithSHA-512AndMGF1Padding",
		"NoPadding"
	};

	private JComboBox<String> keySizeCombo;
	private JComboBox<String> paddingCombo;
	private JTextArea         publicKeyArea;
	private JTextArea         privateKeyArea;
	private JButton           genKeyPairBtn;

	public AsymmetricConfigPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(4, 4));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// ── Top: config options + gen button ──────────────────────────────────
		JPanel topPanel = new JPanel(new BorderLayout(0, 4));
		topPanel.setOpaque(false);

		// Config row
		JPanel configRow = new JPanel(new GridBagLayout());
		configRow.setOpaque(false);
		GridBagConstraints lc = labelConstraints();
		GridBagConstraints fc = fieldConstraints();

		// Key size
		String[] keySizeOptions = new String[KEY_SIZES.length];
		for (int i = 0; i < KEY_SIZES.length; i++)
			keySizeOptions[i] = KEY_SIZES[i] + " bit";
		keySizeCombo = new JComboBox<>(keySizeOptions);
		keySizeCombo.setSelectedIndex(1); // default 2048
		configRow.add(label("Key size:"), at(lc, 0));
		configRow.add(keySizeCombo,       at(fc, 0));

		// Padding
		paddingCombo = new JComboBox<>(PADDINGS);
		paddingCombo.setSelectedIndex(0); // default PKCS1Padding
		configRow.add(label("Padding:"), at(lc, 1));
		configRow.add(paddingCombo,       at(fc, 1));

		// Gen button row
		genKeyPairBtn = new JButton("⚡ Tạo cặp khóa");
		genKeyPairBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		genKeyPairBtn.setFocusPainted(false);
		genKeyPairBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JPanel genRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		genRow.setOpaque(false);
		genRow.add(genKeyPairBtn);

		topPanel.add(configRow, BorderLayout.CENTER);
		topPanel.add(genRow,    BorderLayout.SOUTH);

		// ── Center: key areas ─────────────────────────────────────────────────
		JPanel pubPanel = new JPanel(new BorderLayout(0, 4));
		pubPanel.setBorder(new TitledBorder("Public Key (Công khai)"));
		publicKeyArea = new JTextArea(5, 50);
		publicKeyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		publicKeyArea.setLineWrap(true);
		publicKeyArea.setWrapStyleWord(true);
		pubPanel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);

		JPanel privPanel = new JPanel(new BorderLayout(0, 4));
		privPanel.setBorder(new TitledBorder("Private Key (Bí mật)"));
		privateKeyArea = new JTextArea(5, 50);
		privateKeyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		privateKeyArea.setLineWrap(true);
		privateKeyArea.setWrapStyleWord(true);
		privPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pubPanel, privPanel);
		splitPane.setDividerLocation(150);
		splitPane.setResizeWeight(0.5);

		// ── Assemble ──────────────────────────────────────────────────────────
		add(topPanel,  BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
	}

	// ── GridBagConstraints helpers (same pattern as SymmetricConfigPanel) ─────

	private GridBagConstraints labelConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 0, 4, 10);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		return c;
	}

	private GridBagConstraints fieldConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 0, 4, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		return c;
	}

	private GridBagConstraints at(GridBagConstraints base, int row) {
		GridBagConstraints c = (GridBagConstraints) base.clone();
		c.gridy = row;
		return c;
	}

	private JLabel label(String text) {
		JLabel l = new JLabel(text);
		l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		return l;
	}

	// ── Getters ───────────────────────────────────────────────────────────────

	public int getSelectedKeySize() {
		int idx = keySizeCombo.getSelectedIndex();
		return (idx >= 0) ? KEY_SIZES[idx] : 2048;
	}

	public String getSelectedPadding() {
		return (String) paddingCombo.getSelectedItem();
	}

	/** Transformation string ready for Cipher.getInstance(), e.g. "RSA/ECB/PKCS1Padding" */
	public String getTransformation() {
		return "RSA/ECB/" + getSelectedPadding();
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
