module ru.itmo.grafix {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.base;
    requires javafx.swing;

    opens ru.itmo.grafix to javafx.fxml;
    exports ru.itmo.grafix;
    exports ru.itmo.grafix.core.colorspace.implementation to javafx.fxml;
    exports ru.itmo.grafix.core.colorspace;
    exports ru.itmo.grafix.ui.controllers;
    opens ru.itmo.grafix.ui.controllers to javafx.fxml;
    exports ru.itmo.grafix.ui.components.scrollpane;
    opens ru.itmo.grafix.ui.components.scrollpane to javafx.fxml;
    exports ru.itmo.grafix.ui.components.dialogs;
    opens ru.itmo.grafix.ui.components.dialogs to javafx.fxml;
    exports ru.itmo.grafix.core.imageprocessing;
    opens ru.itmo.grafix.core.imageprocessing to javafx.fxml;
    exports ru.itmo.grafix.core.image;
    opens ru.itmo.grafix.core.image to javafx.fxml;
    exports ru.itmo.grafix.core.dithering;
    opens ru.itmo.grafix.core.dithering to javafx.fxml;
    exports ru.itmo.grafix.core.autocorrection;
    opens ru.itmo.grafix.core.autocorrection to javafx.fxml;
    opens ru.itmo.grafix.core.scaling to javafx.fxml;
    exports ru.itmo.grafix.core.scaling;
    exports ru.itmo.grafix.ui.components.dialogs.filters;
    opens ru.itmo.grafix.ui.components.dialogs.filters to javafx.fxml;
    exports ru.itmo.grafix.core.filtering;
}
