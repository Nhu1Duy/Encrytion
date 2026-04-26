package controller;

import view.MainFrame;

public class CryptoController {

	private final ControllerContext  ctx;
    private final KeyValidator       validator;
    private final HillKeyParser      hillKeyParser;
    private SymmetricController symmetricController;
 
    public CryptoController(MainFrame view) {
        ctx           = new ControllerContext(view);
        validator     = new KeyValidator(ctx);
        hillKeyParser = new HillKeyParser(ctx);
        symmetricController = new SymmetricController(ctx);
    }
    public void bind() {
        new ActionController(ctx, validator).bind();
        new FileController(ctx, hillKeyParser).bind();
        new MenuController(ctx).bind();
        symmetricController.bind();
    }
}
