package controller;

public class MenuController {

	private final ControllerContext ctx;

	public MenuController(ControllerContext ctx) {
		this.ctx = ctx;
	}

	public void bind() {
		bindAlgorithmMenu();
		bindLanguageMenu();
	}

	private void bindAlgorithmMenu() {
		ctx.view.getItemCaesar().addActionListener(e -> switchMethod(ControllerContext.METHOD_CAESAR));
		ctx.view.getItemSubstitution().addActionListener(e -> switchMethod(ControllerContext.METHOD_SUBSTITUTION));
		ctx.view.getItemAffine().addActionListener(e -> switchMethod(ControllerContext.METHOD_AFFINE));
		ctx.view.getItemVigenere().addActionListener(e -> switchMethod(ControllerContext.METHOD_VIGENERE));
		ctx.view.getItemHill().addActionListener(e -> switchMethod(ControllerContext.METHOD_HILL));
		ctx.view.getItemPermutation().addActionListener(e -> switchMethod(ControllerContext.METHOD_PERMUTATION));
		ctx.view.getItemSymmetric().addActionListener(e -> switchMethod(ControllerContext.METHOD_SYMMETRIC));
	}

	private void bindLanguageMenu() {
		ctx.view.getItemVN().addActionListener(e -> {
			ctx.currentLanguage = ControllerContext.LANG_VN;
			ctx.view.setLanguageStatus(ControllerContext.LANG_VN);
		});
		ctx.view.getItemEN().addActionListener(e -> {
			ctx.currentLanguage = ControllerContext.LANG_EN;
			ctx.view.setLanguageStatus(ControllerContext.LANG_EN);
		});
	}

	private void switchMethod(String methodId) {
		ctx.currentMethod = methodId;
		ctx.view.showLayout(methodId);
	}
}
