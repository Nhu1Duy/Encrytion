package controller;

import controller.classic.*;
import controller.symmetric.*;
import view.MainFrame;

public class AppController {

    private final AppContext               ctx;
    private final MenuController           menuController;
    private final ClassicActionController  classicActionController;
    private final ClassicFileController    classicFileController;
    private final SymmetricKeyValidator    symKeyValidator;
    private final SymmetricActionController symmetricActionController;
    private final SymmetricFileController   symmetricFileController;

    public AppController(MainFrame view) {
        ctx = new AppContext(view);

        // Classic
        ClassicKeyValidator classicKeyValidator = new ClassicKeyValidator(ctx);
        HillKeyParser       hillKeyParser       = new HillKeyParser(ctx);
        menuController           = new MenuController(ctx);
        classicActionController  = new ClassicActionController(ctx, classicKeyValidator);
        classicFileController    = new ClassicFileController(ctx, hillKeyParser);

        // Symmetric
        symKeyValidator          = new SymmetricKeyValidator(ctx);
        symmetricActionController = new SymmetricActionController(ctx, symKeyValidator);
        symmetricFileController   = new SymmetricFileController(ctx, symKeyValidator);
    }

    public void bind() {
        menuController.bind();
        classicActionController.bind();
        classicFileController.bind();
        symmetricActionController.bind();
        symmetricFileController.bind();
    }
}