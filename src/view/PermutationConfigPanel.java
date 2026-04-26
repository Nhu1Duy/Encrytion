package view;

import javax.swing.*;
import java.awt.FlowLayout;

public class PermutationConfigPanel extends JPanel implements KeyPanel {
	private JTextField keyField, lenField;
	private JButton genBtn;

	public PermutationConfigPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		keyField = new JTextField(10);
		lenField = new JTextField("5", 3);
		genBtn = new JButton("Gen");

		add(new JLabel("Thứ tự:"));
		add(keyField);
		add(new JLabel(" | Độ dài: "));
		add(lenField);
		add(genBtn);
	}

	/// --- GETTER
	public JTextField getKeyField() {
		return keyField;
	}

	public JTextField getLenField() {
		return lenField;
	}

	public JButton getGenBtn() {
		return genBtn;
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