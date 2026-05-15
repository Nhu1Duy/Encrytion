package view.hash;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HashConfigPanel extends JPanel {

	private JLabel algoLabel;
	private JButton hashTextBtn;
	private JButton hashFileBtn;

	public HashConfigPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 10));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Cấu hình – Hash",
				TitledBorder.LEFT, TitledBorder.TOP));

		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		infoPanel.add(label("Thuật toán:"));
		algoLabel = new JLabel("MD5");
		algoLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
		algoLabel.setForeground(new Color(0, 100, 200));
		infoPanel.add(algoLabel);

		add(infoPanel, BorderLayout.CENTER);

		hashTextBtn = new JButton("# Hash Text");
		hashTextBtn.setFocusPainted(false);
		hashTextBtn.setBackground(new Color(52, 152, 219));
		hashTextBtn.setForeground(Color.WHITE);
		hashTextBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		hashFileBtn = new JButton("📁 Hash File");
		hashFileBtn.setFocusPainted(false);
		hashFileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0)); 
		btnRow.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
		btnRow.add(hashTextBtn);
		btnRow.add(hashFileBtn);

		add(btnRow, BorderLayout.SOUTH);
	}

	private JLabel label(String text) {
		return new JLabel(text);
	}

	public void updateAlgoLabel(String algoName) {
		algoLabel.setText(algoName);
		TitledBorder border = (TitledBorder) getBorder();
		border.setTitle("Cấu hình – " + algoName);
		repaint();
	}

	public JButton getHashTextBtn() { return hashTextBtn; }
	public JButton getHashFileBtn() { return hashFileBtn; }
}