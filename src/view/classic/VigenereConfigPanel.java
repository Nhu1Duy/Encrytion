package view.classic;

import javax.swing.*;

import util.FormatButton;
import view.shared.KeyPanel;

import java.awt.Color;
import java.awt.FlowLayout;

public class VigenereConfigPanel extends JPanel implements KeyPanel {
	private JTextField keyField;
	private JTextField keyLenField;
	private JButton genBtn;

	public VigenereConfigPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		keyField = new JTextField(15);
		keyLenField = new JTextField("8", 3);
		genBtn = new JButton("Gen");
		FormatButton.formatButton(genBtn, new Color(33, 37, 41) );

		add(new JLabel("Từ khóa (String): "));
		add(keyField);
		add(new JLabel(" | Độ dài: "));
		add(keyLenField);
		add(genBtn);
	}

	public JTextField getKeyField() {
		return keyField;
	}

	public JTextField getKeyLenField() {
		return keyLenField;
	}

	public String getTextKeyLenField() {
		return keyLenField.getText().trim();
	}

	public JButton getGenBtn() {
		return genBtn;
	}

	@Override
	public String getKeyText() {
		return keyField.getText().trim();
	}

	@Override
	public void setKeyText(String key) {
		keyField.setText(key.trim());
	}
}