package view.classic;

import javax.swing.*;

import view.shared.KeyPanel;

import java.awt.FlowLayout;

public class AffineConfigPanel extends JPanel implements KeyPanel {
	private JTextField keyA, keyB;
	private JButton genBtn;

	public AffineConfigPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		keyA = new JTextField(3);
		keyB = new JTextField(3);
		genBtn = new JButton("Gen Key");

		add(new JLabel("Hệ số a:"));
		add(keyA);
		add(new JLabel("Hệ số b:"));
		add(keyB);
		add(genBtn);
	}

	/// --- GETTER
	public JTextField getKeyA() {
		return keyA;
	}

	public String getTextKeyA() {
		return keyA.getText();
	}

	public JTextField getKeyB() {
		return keyB;
	}

	public String getTextKeyB() {
		return keyB.getText();
	}

	public JButton getGenBtn() {
		return genBtn;
	}
	
	/// --- KEY PANEL ---
	@Override
	public String getKeyText() {
		String a = keyA.getText().trim();
		String b = keyB.getText().trim();
		if (a.isEmpty() && b.isEmpty())
			return "";
		return a + "," + b;
	}

	@Override
	public void setKeyText(String key) {
		String[] parts = key.trim().split("[,;\\s]+");
		if (parts.length >= 2) {
			keyA.setText(parts[0].trim());
			keyB.setText(parts[1].trim());
		} else {
			keyA.setText(key.trim());
			keyB.setText("");
		}
	}
}