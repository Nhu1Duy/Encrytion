package controller;

import view.MainFrame;
import view.classic.ClassicCipherPanel;

public class MenuController {

	private final AppContext ctx;
	private final MainFrame view;

	public MenuController(AppContext ctx) {
		this.ctx = ctx;
		this.view = ctx.view;
	}

	public void bind() {
		bindClassicMenuItems();
		bindSymmetricMenuItem();
		bindAsymmetricMenuItem();
		bindLanguageMenuItems();
		bindHashMenuItem();
	}

	// ── Classic ───────────────────────────────────────────────────────────────

	private void bindClassicMenuItems() {
		view.itemCaesar.addActionListener(e -> switchClassic(ClassicCipherPanel.CAESAR));
		view.itemSubstitution.addActionListener(e -> switchClassic(ClassicCipherPanel.SUBSTITUTION));
		view.itemAffine.addActionListener(e -> switchClassic(ClassicCipherPanel.AFFINE));
		view.itemVigenere.addActionListener(e -> switchClassic(ClassicCipherPanel.VIGENERE));
		view.itemHill.addActionListener(e -> switchClassic(ClassicCipherPanel.HILL));
		view.itemPermutation.addActionListener(e -> switchClassic(ClassicCipherPanel.PERMUTATION));
	}

	private void switchClassic(String cipherName) {
		ctx.currentMode = AppContext.MODE_CLASSIC;
		ctx.classicMethod = cipherName;
		ctx.view.sidePanel.setFileSectionVisible(false);
	    ctx.view.sidePanel.setCryptoButtonsVisible(true);
		view.switchToClassic(cipherName);
	}

	// ── Symmetric ─────────────────────────────────────────────────────────────

	private void bindSymmetricMenuItem() {
		view.itemSymmetric.addActionListener(e -> {
			ctx.currentMode = AppContext.MODE_SYMMETRIC;
			ctx.view.sidePanel.setFileSectionVisible(true);
		    ctx.view.sidePanel.setCryptoButtonsVisible(true);
			view.switchToSymmetric();
		});
	}
	// ── Asymmetric (RSA) ──────────────────────────────────────────────────────

	private void bindAsymmetricMenuItem() { 
		view.itemAsymmetric.addActionListener(e -> {
			ctx.currentMode = AppContext.MODE_ASYMMETRIC;
			ctx.view.sidePanel.setFileSectionVisible(true);
		    ctx.view.sidePanel.setCryptoButtonsVisible(true);
			view.switchToAsymmetric();
		});
	}
	// ── Hash ──────────────────────────────────────────────────────

	private void bindHashMenuItem() { 
		ctx.view.itemHash.addActionListener(e -> {
		    ctx.currentMode = AppContext.MODE_HASH;
		    ctx.view.sidePanel.showCard("Hash");
		    ctx.view.sidePanel.setFileSectionVisible(false); 
		    ctx.view.sidePanel.setCryptoButtonsVisible(false);
		});
	}
	// ── Ngôn ngữ ──────────────────────────────────────────────────────────────

	private void bindLanguageMenuItems() {
		view.itemVN.addActionListener(e -> {
			ctx.currentLanguage = AppContext.LANG_VN;
			view.setStatus("Ngôn ngữ: Tiếng Việt (VN)");
		});
		view.itemEN.addActionListener(e -> {
			ctx.currentLanguage = AppContext.LANG_EN;
			view.setStatus("Ngôn ngữ: English (EN)");
		});
	}
}