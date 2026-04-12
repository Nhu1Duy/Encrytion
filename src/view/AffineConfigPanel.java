package view;

import java.awt.FlowLayout;
import javax.swing.*;

public class AffineConfigPanel extends JPanel {
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

	public JTextField getKeyA() {
		return keyA;
	}

	public String getTextKeyA() {
		return keyA.getText();
	}

	public JTextField getKeyB() {
		return keyB;
	}
	public String getTextKeyB(){
		return keyB.getText();
	}
	public JButton getGenBtn() {
		return genBtn;
	}
}