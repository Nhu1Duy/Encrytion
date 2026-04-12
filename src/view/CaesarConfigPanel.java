package view;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.*;

public class CaesarConfigPanel extends JPanel {
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

	public JTextField getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField.setText(keyField);
	}
	public String getTextKeyField() {
		return this.keyField.getText();
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
}
