package view.classic;

import javax.swing.*;

import view.shared.KeyPanel;

import java.awt.FlowLayout;

public class CaesarConfigPanel extends JPanel implements KeyPanel {
	private JTextField keyField;
	private JTextField keyLenField;
	private JButton genBtn;

	public CaesarConfigPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		keyField = new JTextField(5);
		keyLenField = new JTextField("188", 3);
		genBtn = new JButton("Gen Key");

		add(new JLabel("Bước nhảy: "));
		add(keyField);
		add(new JLabel(" | Giới hạn: "));
		add(keyLenField);
		add(genBtn);
	}

	/// --- GETTER ---
	public JTextField getKeyField() {
		return keyField;
	}

	public String getTextKeyField() {
		return keyField.getText();
	}

	public JTextField getKeyLenField() {
		return keyLenField;
	}

	public String getTextKeyLenField() {
		return keyLenField.getText();
	}

	public JButton getGenBtn() {
		return genBtn;
	}

	public void setKeyField(String v) {
		keyField.setText(v);
	}

	/// --- KEY PANEL ---
	@Override
	public String getKeyText() {
		return keyField.getText().trim();
	}

	@Override
	public void setKeyText(String key) {
		keyField.setText(key.trim());
	}
}