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

	public SidePanel() {
		setLayout(new BorderLayout(0, 10));
		setPreferredSize(new Dimension(310, 0));
		setBorder(new EmptyBorder(10, 10, 10, 5));

		cardPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình Thuật toán"));

		JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
		btnPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
		styleBtn(encryptBtn, new Color(0, 123, 255));
		styleBtn(decryptBtn, new Color(80, 80, 80));
		btnPanel.add(encryptBtn);
		btnPanel.add(decryptBtn);

		add(cardPanel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
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


	private void styleBtn(JButton btn, Color bg) {
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
	}

	// ── getters ──────────────────────────────────────────────────────────────

	public JButton getEncryptBtn() {
		return encryptBtn;
	}

	public JButton getDecryptBtn() {
		return decryptBtn;
	}
}