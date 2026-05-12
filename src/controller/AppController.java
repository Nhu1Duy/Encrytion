package controller;

import controller.classic.ClassicActionController;
import controller.classic.ClassicFileController;
import controller.classic.ClassicKeyValidator;
import controller.classic.HillKeyParser;
import controller.symmetric.SymmetricActionController;
import view.MainFrame;


public class AppController {

    private final AppContext               ctx;
    private final MenuController           menuController;
    private final ClassicActionController  classicActionController;
    private final ClassicFileController    classicFileController;
    private final SymmetricActionController symmetricActionController;

    public AppController(MainFrame view) {
        ctx = new AppContext(view);

        // Classic helpers
        ClassicKeyValidator validator     = new ClassicKeyValidator(ctx);
        HillKeyParser       hillKeyParser = new HillKeyParser(ctx);

        // Khởi tạo controllers
        menuController            = new MenuController(ctx);
        classicActionController   = new ClassicActionController(ctx, validator);
        classicFileController     = new ClassicFileController(ctx, hillKeyParser);
        symmetricActionController = new SymmetricActionController(ctx);
    }

    /** Bind tất cả event listeners — gọi một lần sau khi frame đã visible */
    public void bind() {
        menuController.bind();
        classicActionController.bind();
        classicFileController.bind();
        symmetricActionController.bind();
    }
}
