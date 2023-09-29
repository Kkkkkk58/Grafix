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
    exports ru.itmo.grafix.colorSpace to javafx.fxml;
    exports ru.itmo.grafix.api;
}