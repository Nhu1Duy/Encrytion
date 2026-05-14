package view.shared;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SidePanel extends JPanel {

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel cardPanel = new JPanel(cardLayout);
	private final JButton encryptBtn = new JButton("ENCRYPT ▲");
	private final JButton decryptBtn = new JButton("DECRYPT ▼");

	// ── File section ─────────────────────────────────────────────────────────
	private final JPanel fileSection = new JPanel();
	private final JTextField inputPathField = new JTextField();
	private final JTextField outputPathField = new JTextField();
	private final JButton browseInputBtn = new JButton("📂");
	private final JButton browseOutputBtn = new JButton("📂");
	private final JButton encryptFileBtn = new JButton("🔒 Mã hóa File");
	private final JButton decryptFileBtn = new JButton("🔓 Giải mã File");

	public SidePanel() {
		setLayout(new BorderLayout(0, 10));
		setPreferredSize(new Dimension(310, 0));
		setBorder(new EmptyBorder(10, 10, 10, 5));

		cardPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình Thuật toán"));

		// ── Text encrypt/decrypt buttons ─────────────────────────────────────
		JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
		btnPanel.setBorder(new EmptyBorder(0, 0, 6, 0));
		styleBtn(encryptBtn, new Color(0, 123, 255));
		styleBtn(decryptBtn, new Color(80, 80, 80));
		btnPanel.add(encryptBtn);
		btnPanel.add(decryptBtn);

		// ── File section ─────────────────────────────────────────────────────
		buildFileSection();

		// ── South: btnPanel + fileSection stacked ────────────────────────────
		JPanel southPanel = new JPanel(new BorderLayout(0, 0));
		southPanel.add(btnPanel, BorderLayout.NORTH);
		southPanel.add(fileSection, BorderLayout.CENTER);

		add(cardPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private void buildFileSection() {
		fileSection.setLayout(new GridBagLayout());
		fileSection.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Mã hóa / Giải mã File"));
		fileSection.setVisible(false); // ẩn khi ở Classic mode

		GridBagConstraints lc = new GridBagConstraints();
		lc.gridx = 0;
		lc.anchor = GridBagConstraints.WEST;
		lc.insets = new Insets(3, 4, 3, 4);
		lc.fill = GridBagConstraints.NONE;

		GridBagConstraints fc = new GridBagConstraints();
		fc.gridx = 1;
		fc.fill = GridBagConstraints.HORIZONTAL;
		fc.weightx = 1.0;
		fc.insets = new Insets(3, 0, 3, 2);

		GridBagConstraints bc = new GridBagConstraints();
		bc.gridx = 2;
		bc.insets = new Insets(3, 2, 3, 4);

		// Row 0 — Input
		inputPathField.setEditable(false);
		inputPathField.setToolTipText("File cần mã hóa / giải mã");
		lc.gridy = 0;
		fc.gridy = 0;
		bc.gridy = 0;
		fileSection.add(new JLabel("Input:"), lc);
		fileSection.add(inputPathField, fc);
		fileSection.add(browseInputBtn, bc);

		// Row 1 — Output
		outputPathField.setEditable(false);
		outputPathField.setToolTipText("File kết quả sẽ được lưu tại đây");
		lc.gridy = 1;
		fc.gridy = 1;
		bc.gridy = 1;
		fileSection.add(new JLabel("Output:"), lc);
		fileSection.add(outputPathField, fc);
		fileSection.add(browseOutputBtn, bc);

		// Row 2 — action buttons
		styleBtn(encryptFileBtn, new Color(0, 153, 76));
		styleBtn(decryptFileBtn, new Color(153, 76, 0));
		JPanel fileBtnRow = new JPanel(new GridLayout(1, 2, 8, 0));
		fileBtnRow.add(encryptFileBtn);
		fileBtnRow.add(decryptFileBtn);

		GridBagConstraints row2 = new GridBagConstraints();
		row2.gridy = 2;
		row2.gridx = 0;
		row2.gridwidth = 3;
		row2.fill = GridBagConstraints.HORIZONTAL;
		row2.insets = new Insets(4, 4, 4, 4);
		fileSection.add(fileBtnRow, row2);
	}

	// ── API ──────────────────────────────────────────────────────────────────

	public void addCard(JPanel panel, String key) {
		cardPanel.add(panel, key);
	}

	public void showCard(String key) {
		cardLayout.show(cardPanel, key);
		TitledBorder tb = (TitledBorder) cardPanel.getBorder();
		if (tb != null) {
			tb.setTitle("Thuật toán: " + key);
			cardPanel.repaint();
		}
	}

	/** Gọi từ MenuController khi chuyển mode */
	public void setFileSectionVisible(boolean visible) {
		fileSection.setVisible(visible);
		revalidate();
		repaint();
	}

	public void setCryptoButtonsVisible(boolean visible) {
		encryptBtn.setVisible(visible);
		decryptBtn.setVisible(visible);
		revalidate();
		repaint();
	}

	private void styleBtn(JButton btn, Color bg) {
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
	}

	// ── Getters ──────────────────────────────────────────────────────────────

	public JButton getEncryptBtn() {
		return encryptBtn;
	}

	public JButton getDecryptBtn() {
		return decryptBtn;
	}

	public JButton getBrowseInputBtn() {
		return browseInputBtn;
	}

	public JButton getBrowseOutputBtn() {
		return browseOutputBtn;
	}

	public JButton getEncryptFileBtn() {
		return encryptFileBtn;
	}

	public JButton getDecryptFileBtn() {
		return decryptFileBtn;
	}

	public JTextField getInputPathField() {
		return inputPathField;
	}

	public JTextField getOutputPathField() {
		return outputPathField;
	}
}