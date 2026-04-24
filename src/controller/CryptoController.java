package controller;
import model.clasic.AffineCypher;
import model.clasic.CaesarCypher;
import model.clasic.HillCypher;
import model.clasic.PermutationCypher;
import model.clasic.SubstitutionCipher;
import model.clasic.VigenereCypher;
import view.MainFrame;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

public class CryptoController {
	private MainFrame view;
	private CaesarCypher caesarCypher;
	private SubstitutionCipher substitutionCipher;
	private VigenereCypher vigenereCipher;
	private AffineCypher affineCypher;
	private HillCypher hillCyper;
	private PermutationCypher permutationCypher;
	private String currentMethod = "Dịch Chuyển";
	private String currentLanguage = "VN";

	public CryptoController(MainFrame view) {
		this.view = view;
		this.caesarCypher = new CaesarCypher();
		this.substitutionCipher = new SubstitutionCipher();
		this.vigenereCipher = new VigenereCypher();
		this.affineCypher = new AffineCypher();
		this.hillCyper = new HillCypher();
		this.permutationCypher = new PermutationCypher();

		view.getItemCaesar().addActionListener(e -> switchMethod("Dịch Chuyển", "Caesar"));
		view.getItemVigenere().addActionListener(e -> switchMethod("Vigenere", "Vigenere"));
		view.getItemSubstitution().addActionListener(e -> switchMethod("Thay Thế", "Substitution"));
		view.getItemAffine().addActionListener(e -> switchMethod("Affine", "Affine (Mã hóa tuyến tính)"));
		view.getItemHill().addActionListener(e -> switchMethod("Hill", "Hill (Mã hóa ma trận)"));
		view.getItemPermutation().addActionListener(e -> switchMethod("Hoán Vị", "Hoán vị (Permutation)"));
		view.getItemVN().addActionListener(e -> {
			currentLanguage = "VN";
			view.setLanguageStatus("VN");
		});

		view.getItemEN().addActionListener(e -> {
			currentLanguage = "EN";
			view.setLanguageStatus("EN");
		});
		view.getEncryptBtn().addActionListener(e -> handleAction(true));
		view.getDecryptBtn().addActionListener(e -> handleAction(false));
		
		/// --- GENKEY ---
		view.getCaesarPanel().getGenBtn().addActionListener(e -> {
			try {
				String maxStr = view.getCaesarPanel().getTextKeyLenField().trim();
				int max = maxStr.isEmpty() ? 188 : Integer.parseInt(maxStr);
				int randomKey = caesarCypher.genKey(max);

				view.getCaesarPanel().setKeyField((String.valueOf(randomKey)));
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Giới hạn phải là một số nguyên dương!");
			}
		});
		view.getSubstitutionPanel().getGenBtn().addActionListener(e -> {
			String alphabet = currentLanguage.equals("VN") ? Tool.Alphabet.VN_ALPHABET_FUL
					: Tool.Alphabet.EN_ALPHABET_FUL;
			String randomKey = SubstitutionCipher.genKeySubstitutionCipher(alphabet);

			view.getSubstitutionPanel().getKeyArea().setText(randomKey);
		});
		view.getAffinePanel().getGenBtn().addActionListener(e -> {
			int n = currentLanguage.equals("VN") ? Tool.Alphabet.VN_ALPHABET_FUL.length()
					: Tool.Alphabet.EN_ALPHABET_FUL.length();

			int[] keys = affineCypher.genKey(n);

			view.getAffinePanel().getKeyA().setText(String.valueOf(keys[0]));
			view.getAffinePanel().getKeyB().setText(String.valueOf(keys[1]));
		});

		view.getVigenerePanel().getGenBtn().addActionListener(e -> {
			try {
				String lenStr = view.getVigenerePanel().getTextKeyLenField();
				int length = lenStr.isEmpty() ? 8 : Integer.parseInt(lenStr);
				String alphabet = currentLanguage.equals("VN") ? Tool.Alphabet.VN_ALPHABET_FUL
						: Tool.Alphabet.EN_ALPHABET_FUL;

				String randomKey = vigenereCipher.genKey(alphabet, length);
				view.getVigenerePanel().getKeyField().setText(randomKey);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Độ dài khóa phải là số nguyên dương!");
			}
		});
		view.getPermutationPanel().getGenBtn().addActionListener(e -> {
			try {
				String lenStr = view.getPermutationPanel().getLenField().getText();
				int length = lenStr.isEmpty() ? 5 : Integer.parseInt(lenStr);

				String randomKey = permutationCypher.genKey(length);

				 view.getPermutationPanel().getKeyField().setText(randomKey);

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Độ dài khóa hoán vị phải là số nguyên!");
			}
		});
	}

	private void switchMethod(String methodId, String title) {
		this.currentMethod = methodId;
		//view.setMethodTitle(title);
		view.showLayout(methodId);
	}

	private void handleAction(boolean isEncrypt) {
		String input = view.getInputArea().getText().trim();

		if (input.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập văn bản!");
			return;
		}

		try {
			String result = "";

			switch (currentMethod) {

			/// --- CAESAR ---
			case "Dịch Chuyển": {
				String keyStr = view.getCaesarPanel().getTextKeyField().trim();
				if (keyStr.isEmpty()) {
					throw new Exception("Vui lòng nhập Key hoặc nhấn 'Gen' để tạo khóa!");
				}
				int k = Integer.parseInt(keyStr);

				if (currentLanguage.equals("VN")) {
					result = isEncrypt ? caesarCypher.encryptVN(input, k) : caesarCypher.decryptVN(input, k);
				} else {
					result = isEncrypt ? caesarCypher.encryptEN(input, k) : caesarCypher.decryptEN(input, k);
				}
				break;
			}


			/// ---  SUBSTITUTION ---
			case "Thay Thế": {
			    try {
			        String key = view.getSubstitutionPanel().getKeyArea().getText();
			        if (key == null || key.isEmpty())
			            throw new Exception("Key không được để trống");
			        
			        String alphabet = currentLanguage.equals("VN") ? Tool.Alphabet.VN_ALPHABET_FUL : Tool.Alphabet.EN_ALPHABET_FUL;
			        int requiredLength = alphabet.length();

			        if (key.length() != requiredLength) {
			            throw new Exception("Key phải dài đúng " + requiredLength + " kí tự");
			        }

			        Set<Character> charSet = new HashSet<>();
			        for (char c : key.toCharArray()) {
			            if (alphabet.indexOf(c) == -1) {
			                throw new Exception("Ký tự '" + c + "' không nằm trong bảng chữ cái nguồn");
			            }
			            if (!charSet.add(c)) {
			                throw new Exception("Khóa không được chứa ký tự lặp lại: " + c);
			            }
			        }
			        if (currentLanguage.equals("VN")) {
			            result = isEncrypt ? substitutionCipher.encryptVN(input, key)
			                               : substitutionCipher.decryptVN(input, key);
			        } else {
			            result = isEncrypt ? substitutionCipher.encryptEN(input, key)
			                               : substitutionCipher.decryptEN(input, key);
			        }
			        
			    } catch (Exception e) {
			        throw new Exception(e.getMessage()); 
			    }
			    break;
			}

			/// --- AFFINE --- 
			case "Affine": {
				String aStr = view.getAffinePanel().getTextKeyA().trim();
				String bStr = view.getAffinePanel().getTextKeyB().trim();

				if (aStr.isEmpty() || bStr.isEmpty()) {
					throw new Exception("Key a và b không được để trống");
				}

				int a = Integer.parseInt(aStr);
				int b = Integer.parseInt(bStr);
				if (a == 1 && b == 0 || b == 1 && a == 0) {
					throw new Exception(" Bộ khóa (1, 0) sẽ không làm thay đổi văn bản!");
				}
				int inverse = currentLanguage.equals("VN") ? affineCypher.reverseVN(a) : affineCypher.reverseEN(a);

				if (inverse == -1) {
					throw new Exception("Giá trị a không hợp lệ (không có nghịch đảo modulo)");
				}

				if (currentLanguage.equals("VN")) {
					result = isEncrypt ? affineCypher.encryptVN(input, a, b) : affineCypher.decryptVN(input, a, b);
				} else {
					result = isEncrypt ? affineCypher.encryptEN(input, a, b) : affineCypher.decryptEN(input, a, b);
				}
				break;
			}

			/// ---  VIGENERE --- 
			case "Vigenere": {
				String key = view.getVigenerePanel().getKeyField().getText().trim();
				if (key.isEmpty())
					throw new Exception("Key không được để trống");

				if (currentLanguage.equals("VN")) {
					result = isEncrypt ? vigenereCipher.encryptVN(input, key) : vigenereCipher.decryptVN(input, key);
				} else {
					result = isEncrypt ? vigenereCipher.encryptEN(input, key) : vigenereCipher.decryptEN(input, key);
				}
				break;
			}

			/// ---  HILL --- 
			case "Hill": {
				String hillKey = view.getHillPanel().getKeyField().getText().trim();
				if (hillKey.isEmpty()) {
					throw new Exception("Key Hill không được để trống");
				}

				result = "🚧 ARE YOU BLIND ---- YOU PIGGYYYYYYYYYYYYYYYYY";
				break;
			}

			/// --- PERMUTATION --- 
			case "Hoán Vị": {
				String permKey = view.getPermutationPanel().getKeyField().getText().trim();
				if (permKey.isEmpty()) {
					throw new Exception("Key hoán vị không được để trống");
				}

				result = isEncrypt ? permutationCypher.encrypt(input, permKey)
						: permutationCypher.decrypt(input, permKey);
				break;
			}

			///--- DEFAULT --- 
			default:
				throw new Exception("Phương pháp không hợp lệ");
			}

			view.getOutputArea().setText(result);

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Key phải là số hợp lệ!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
		}
	}

}