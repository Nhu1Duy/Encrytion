package view.shared;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import util.FormatButton;

import java.awt.*;
import java.text.Format;

public class SidePanel extends JPanel {

	private final CardLayout cards = new CardLayout();
	private final JPanel pnlConfig = new JPanel(cards);

	private final JButton btnTextEnc = new JButton("ENCRYPT ▲");
	private final JButton btnTextDec = new JButton("DECRYPT ▼");

	private final JPanel sectionFile = new JPanel();
	private final JTextField txtInput = new JTextField();
	private final JTextField txtOutput = new JTextField();
	private final JButton btnOpenIn = new JButton("📂");
	private final JButton btnOpenOut = new JButton("📂");
	private final JButton btnFileEnc = new JButton("🔒 Mã hóa File");
	private final JButton btnFileDec = new JButton("🔓 Giải mã File");

	public SidePanel() {
		setLayout(new BorderLayout(0, 10));
		setPreferredSize(new Dimension(310, 0));
		setBorder(new EmptyBorder(10, 10, 10, 5));

		pnlConfig.setBorder(BorderFactory.createTitledBorder("Cấu hình Thuật toán"));

		JPanel pnlTextActions = new JPanel(new GridLayout(1, 2, 10, 0));
		pnlTextActions.setBorder(new EmptyBorder(0, 0, 6, 0));
		FormatButton.formatButton(btnTextEnc, new Color(0, 123, 255));
		FormatButton.formatButton(btnTextDec, new Color(80, 80, 80));
		pnlTextActions.add(btnTextEnc);
		pnlTextActions.add(btnTextDec);

		initFileSection();

		JPanel pnlSouth = new JPanel(new BorderLayout(0, 5));
		pnlSouth.add(pnlTextActions, BorderLayout.NORTH);
		pnlSouth.add(sectionFile, BorderLayout.CENTER);

		add(pnlConfig, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void initFileSection() {
		sectionFile.setLayout(new BoxLayout(sectionFile, BoxLayout.Y_AXIS));
		sectionFile.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Mã hóa / Giải mã File"));
		sectionFile.setVisible(false);

		sectionFile.add(createFileRow("Input:", txtInput, btnOpenIn));
		sectionFile.add(Box.createVerticalStrut(5));

		sectionFile.add(createFileRow("Output:", txtOutput, btnOpenOut));
		sectionFile.add(Box.createVerticalStrut(10));

		JPanel pnlFileBtns = new JPanel(new GridLayout(1, 2, 8, 0));
		FormatButton.formatButton(btnFileEnc, new Color(0, 153, 76));
		FormatButton.formatButton(btnFileDec, new Color(153, 76, 0));
		pnlFileBtns.add(btnFileEnc);
		pnlFileBtns.add(btnFileDec);

		sectionFile.add(pnlFileBtns);
	}

	private JPanel createFileRow(String label, JTextField field, JButton btn) {
		JPanel row = new JPanel(new BorderLayout(5, 0));
		JLabel lbl = new JLabel(label);
		lbl.setPreferredSize(new Dimension(50, 0));

		field.setEditable(false);
		row.add(lbl, BorderLayout.WEST);
		row.add(field, BorderLayout.CENTER);
		row.add(btn, BorderLayout.EAST);
		return row;
	}

	public void addCard(JPanel panel, String key) {
		pnlConfig.add(panel, key);
	}

	public void showCard(String key) {
		cards.show(pnlConfig, key);
		if (pnlConfig.getBorder() instanceof TitledBorder) {
			((TitledBorder) pnlConfig.getBorder()).setTitle("Thuật toán: " + key);
			pnlConfig.repaint();
		}
	}

	public void setFileSectionVisible(boolean visible) {
		sectionFile.setVisible(visible);
		revalidate();
		repaint();
	}

	public void setCryptoButtonsVisible(boolean visible) {
		btnTextEnc.setVisible(visible);
		btnTextDec.setVisible(visible);
		revalidate();
		repaint();
	}

	public JButton getEncryptBtn() {
		return btnTextEnc;
	}

	public JButton getDecryptBtn() {
		return btnTextDec;
	}

	public JButton getBrowseInputBtn() {
		return btnOpenIn;
	}

	public JButton getBrowseOutputBtn() {
		return btnOpenOut;
	}

	public JButton getEncryptFileBtn() {
		return btnFileEnc;
	}

	public JButton getDecryptFileBtn() {
		return btnFileDec;
	}

	public JTextField getInputPathField() {
		return txtInput;
	}

	public JTextField getOutputPathField() {
		return txtOutput;
	}
}