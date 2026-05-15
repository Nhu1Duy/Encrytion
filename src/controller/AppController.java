package controller;

import controller.asymmetric.AsymmetricActionController;
import controller.asymmetric.AsymmetricFileController;
import controller.classic.*;
import controller.hash.HashActionController;
import controller.hash.HashFileController;
import controller.symmetric.*;
import view.MainFrame;

public class AppController {

	private final AppContext ctx;
	private final MenuController menuController;
	private final ClassicActionController classicActionController;
	private final ClassicFileController classicFileController;
	private final SymmetricKeyValidator symKeyValidator;
	private final SymmetricActionController symmetricActionController;
	private final SymmetricFileController symmetricFileController;
	private final AsymmetricActionController asymmetricActionController;
	private final AsymmetricFileController asymmetricFileController;
	private final HashActionController hashActionController;
	private final HashFileController hashFileController;

	public AppController(MainFrame view) {
		ctx = new AppContext(view);

		ClassicKeyValidator classicKeyValidator = new ClassicKeyValidator(ctx);
		menuController           = new MenuController(ctx);
		classicActionController  = new ClassicActionController(ctx, classicKeyValidator);
		classicFileController    = new ClassicFileController(ctx);  

		symKeyValidator          = new SymmetricKeyValidator(ctx);
		symmetricActionController = new SymmetricActionController(ctx, symKeyValidator);
		symmetricFileController  = new SymmetricFileController(ctx, symKeyValidator);

		asymmetricActionController = new AsymmetricActionController(ctx);
		asymmetricFileController   = new AsymmetricFileController(ctx);

		hashActionController = new HashActionController(ctx);
		hashFileController   = new HashFileController(ctx, hashActionController);
	}

	public void bind() {
		menuController.bind();
		classicActionController.bind();
		classicFileController.bind();
		symmetricActionController.bind();
		symmetricFileController.bind();
		asymmetricActionController.bind();
		asymmetricFileController.bind();
		hashActionController.bind();
		hashFileController.bind();
	}
}